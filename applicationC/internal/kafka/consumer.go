package kafka

import (
	"applicationC/config"
	"fmt"
	"log"

	"github.com/confluentinc/confluent-kafka-go/kafka"
)

// StartConsumer initializes a Kafka consumer and starts consuming messages
func StartConsumer(cfg *config.Config) error {
	// Define Kafka consumer configuration
	config := &kafka.ConfigMap{
		"bootstrap.servers": cfg.KafkaBrokers,
		"group.id":          cfg.ConsumerGroup,
		"auto.offset.reset": "earliest",
	}

	// Create a new Kafka consumer
	consumer, err := kafka.NewConsumer(config)
	if err != nil {
		return fmt.Errorf("failed to create consumer: %w", err)
	}
	defer consumer.Close()

	// Subscribe to the specified topic
	topic := "test_topic"
	err = consumer.SubscribeTopics([]string{topic}, nil)
	if err != nil {
		return fmt.Errorf("failed to subscribe to topics: %w", err)
	}

	log.Printf("Consumer started. Listening to topic: %s", topic)

	// Consume messages in an infinite loop
	for {
		msg, err := consumer.ReadMessage(-1) // -1 means blocking indefinitely until a message is received
		if err == nil {
			// Print the received message
			log.Printf("Received message: %s from topic: %s\n", string(msg.Value), msg.TopicPartition)
		} else {
			// Log errors (e.g., if consumer is closed or topic is not available)
			log.Printf("Error consuming message: %v\n", err)
		}
	}
}
