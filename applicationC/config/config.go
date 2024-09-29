package config

// Config stores the application configuration values
type Config struct {
	KafkaBrokers  string
	ConsumerGroup string
	Topic         string
}

// LoadConfig loads configuration values for the application
func LoadConfig() *Config {
	return &Config{
		KafkaBrokers:  "localhost:9092",
		ConsumerGroup: "go-consumer-group",
		Topic:         "student",
	}
}
