package main

import (
	"applicationC/config"
	"applicationC/internal/kafka"
	"log"
)

func main() {
	// Load configuration values
	cfg := config.LoadConfig()

	// Pass configuration values to the Kafka consumer
	err := kafka.StartConsumer(cfg)
	if err != nil {
		log.Fatalf("Error starting Kafka consumer: %v", err)
	}
}
