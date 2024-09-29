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

var serviceName = semconv.ServiceNameKey.String("applicationC")

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
	defer consumer.Close()

	res, err := resource.New(ctx,
		resource.WithAttributes(
			serviceName,
		),
	)
	if err != nil {
		log.Fatal(err)
	}
	shutdownTracerProvider, err := tracing.InitTracerProvider(ctx, res)
	if err != nil {
		log.Fatal(err)
	}
	defer func() {
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

	// Consume messages in an infinite loop
	for {
		msg, err := consumer.ReadMessage(-1) // Blocking until a message is received
		if err == nil {
			// Extract tracing context directly from Kafka headers using MapCarrier
			ctx := extractContextFromHeaders(msg)

			// Create a new span using the extracted context
			tracer := otel.Tracer("kafka manual instrumentation")
			ctx, span := tracer.Start(ctx, "consume", trace.WithSpanKind(trace.SpanKindConsumer))
			span.SetAttributes(
				attribute.String("messaging.system", "kafka"),
				attribute.String("messaging.destination", *msg.TopicPartition.Topic),
				attribute.Int64("messaging.kafka.partition", int64(msg.TopicPartition.Partition)),
				attribute.String("messaging.kafka.message_key", string(msg.Key)),
			)

			// Log the received message and trace context
			log.Printf("Received message: %s from topic: %s", string(msg.Value), msg.TopicPartition.Topic)
			log.Printf("Trace ID: %s, Span ID: %s", span.SpanContext().TraceID(), span.SpanContext().SpanID())

			// End the span after processing
			span.End()
		} else {
			log.Printf("Error consuming message: %v", err)
		}
	}
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
