package config

type Config struct {
	RabbitMQ RabbitMQ
	Jaeger   OpenTelemetry
}

type RabbitMQ struct {
	Host            string
	Port            string
	User            string
	Password        string
	Exchange        string
	ConsumeSettings QueueSettings
	ProduceSettings QueueSettings
	ConsumerTag     string
	WorkerPoolSize  int
}

type OpenTelemetry struct {
	JaegerHost  string
	ServiceName string
	LogSpans    bool
}

type QueueSettings struct {
	QueueName  string
	RoutingKey string
}
