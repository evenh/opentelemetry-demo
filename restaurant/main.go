package main

import (
	"os"
	"runtime"

	"github.com/evenh/opentelemetry-demo/restaurant/config"
	"github.com/evenh/opentelemetry-demo/restaurant/internal"
	"github.com/evenh/opentelemetry-demo/restaurant/pkg/opentelemetry"
	"github.com/evenh/opentelemetry-demo/restaurant/pkg/rabbitmq"
	"github.com/sirupsen/logrus"
	"go.opentelemetry.io/otel"
)

const (
	exchangeName  = "orders-exchange"
	receivedQueue = "orders.received"
	statusQueue   = "orders.status-updates"
)

var (
	logger = logrus.New()
)

func main() {
	logrus.SetOutput(os.Stdout)
	logger.Info("Starting server")
	cfg := createConfig()

	conn, err := rabbitmq.NewRabbitMQConn(cfg)
	if err != nil {
		logger.Fatalf("cannot create rabbit connection: %+v", err)
	}
	defer conn.Close()

	tracerProvider, err := opentelemetry.InitOtel(cfg)
	if err != nil {
		logger.Fatalf("cannot create tracer: %+v", err)
	}
	otel.SetTracerProvider(tracerProvider)

	s := internal.NewRestaurantServer(conn, logger, cfg)
	s.Run()
}

func createConfig() *config.Config {
	return &config.Config{
		RabbitMQ: config.RabbitMQ{
			Host:     getEnv("AMQP_HOST", "localhost"),
			Port:     "5672",
			User:     "app",
			Password: "superSecret",
			Exchange: exchangeName,
			ConsumeSettings: config.QueueSettings{
				QueueName:  receivedQueue,
				RoutingKey: "RECEIVE",
			},
			ProduceSettings: config.QueueSettings{
				QueueName:  statusQueue,
				RoutingKey: "STATUS",
			},
			ConsumerTag:    "restaurant-orders-consumer-golang",
			WorkerPoolSize: runtime.NumCPU(),
		},
		Jaeger: config.OpenTelemetry{
			JaegerHost:  "http://" + getEnv("JAEGER_HOST", "localhost") + ":14268/api/traces",
			ServiceName: "restaurant",
			LogSpans:    true,
		},
	}
}
