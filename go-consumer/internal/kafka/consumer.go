package kafka

import (
	"applicationC/config"
	"applicationC/internal/tracing"
	"context"
	"fmt"
	"github.com/confluentinc/confluent-kafka-go/kafka"
	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/attribute"
	"go.opentelemetry.io/otel/propagation"
	"go.opentelemetry.io/otel/sdk/resource"
	semconv "go.opentelemetry.io/otel/semconv/v1.26.0"
	"go.opentelemetry.io/otel/trace"
	"log"
)

var serviceName = semconv.ServiceNameKey.String("student-mailing")
var serviceNamespace = semconv.ServiceNamespaceKey.String("student")
var serviceVersion = semconv.ServiceVersionKey.String("1.0.0")
var deploymentEnvironment = semconv.DeploymentEnvironmentKey.String("development")

// StartConsumer initializes a Kafka consumer and starts consuming messages
func StartConsumer(ctx context.Context, cfg *config.Config) error {
	// Define Kafka consumer configuration
	kafkaConfig := &kafka.ConfigMap{
		"bootstrap.servers": cfg.KafkaBrokers,
		"group.id":          cfg.ConsumerGroup,
		"auto.offset.reset": "earliest",
	}

	// Create a new Kafka consumer
	consumer, err := kafka.NewConsumer(kafkaConfig)
	if err != nil {
		return fmt.Errorf("failed to create consumer: %w", err)
	}
	defer consumer.Close() // Ensure the consumer is closed on function exit

	// Create OpenTelemetry resource attributes for tracing, e.g. serviceName, namespace, version
	res, err := resource.New(ctx,
		resource.WithAttributes(
			serviceName,
			serviceNamespace,
			serviceVersion,
			deploymentEnvironment,
		),
	)
	if err != nil {
		log.Fatal(err) // Handle error in initializing resource
	}

	// Initialize OpenTelemetry TracerProvider for distributed tracing
	shutdownTracerProvider, err := tracing.InitTracerProvider(ctx, res)
	if err != nil {
		log.Fatal(err)
	}
	defer func() {
		// Ensure the TracerProvider is properly shut down
		if err := shutdownTracerProvider(ctx); err != nil {
			log.Fatalf("failed to shutdown TracerProvider: %s", err)
		}
	}()

	// Subscribe to the specified topic
	topic := cfg.Topic
	err = consumer.SubscribeTopics([]string{topic}, nil)
	if err != nil {
		return fmt.Errorf("failed to subscribe to topics: %w", err)
	}

	log.Printf("Consumer started. Listening to topic: %s", topic)

	// Start message consumption loop
	consumeMessages(ctx, consumer)

	return nil
}

func consumeMessages(ctx context.Context, consumer *kafka.Consumer) {
	for {
		select {
		case <-ctx.Done():
			log.Println("Shutting down consumer...")
			return // Exit loop on context cancellation
		default:
			msg, err := consumer.ReadMessage(-1) // Blocking call until a message is received
			if err == nil {
				processMessage(ctx, msg)
			} else {
				log.Printf("Error consuming message: %v", err)
			}
		}
	}
}

// processMessage processes each received Kafka message, extracts tracing context, and creates spans
func processMessage(ctx context.Context, msg *kafka.Message) {
	// Extract tracing context from Kafka headers
	ctx = extractContextFromHeaders(msg)

	// Start a new span for processing the consumed message
	tracer := otel.Tracer("kafka manual instrumentation")
	ctx, span := tracer.Start(ctx, "consume", trace.WithSpanKind(trace.SpanKindConsumer))

	// Set span attributes related to the Kafka message
	span.SetAttributes(
		attribute.String("messaging.system", "kafka"),
		attribute.String("messaging.destination", *msg.TopicPartition.Topic),
		attribute.Int64("messaging.kafka.partition", int64(msg.TopicPartition.Partition)),
		attribute.String("messaging.kafka.message_key", string(msg.Key)),
	)

	// Log message details
	log.Printf("Received message: %s from topic: %s", string(msg.Value), msg.TopicPartition.Topic)
	log.Printf("Trace ID: %s, Span ID: %s", span.SpanContext().TraceID(), span.SpanContext().SpanID())

	// End the span after processing
	span.End()
}

// extractContextFromHeaders extracts the OpenTelemetry context from Kafka message headers.
func extractContextFromHeaders(msg *kafka.Message) context.Context {
	// Prepare a MapCarrier to hold the headers
	carrier := make(map[string]string)

	// Copy Kafka headers into the MapCarrier
	for _, header := range msg.Headers {
		carrier[header.Key] = string(header.Value)
	}

	// Use OpenTelemetry propagator to extract the context
	propagator := otel.GetTextMapPropagator()
	ctx := propagator.Extract(context.Background(), propagation.MapCarrier(carrier))

	return ctx
}
