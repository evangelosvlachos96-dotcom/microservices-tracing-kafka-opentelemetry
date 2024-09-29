// cmd/consumer/main.go
package main

import (
	"applicationC/internal/kafka"
	"log"
)

func main() {
	// Create a new Kafka consumer and start consuming messages
	err := kafka.StartConsumer()
	if err != nil {
		log.Fatalf("Error starting Kafka consumer: %v", err)
	}
}
