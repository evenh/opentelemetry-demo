package internal

import (
	"context"
	"os"
	"os/signal"
	"syscall"

	"github.com/evenh/opentelemetry-demo/restaurant/config"
	"github.com/evenh/opentelemetry-demo/restaurant/pkg/rabbitmq"
	"github.com/sirupsen/logrus"
	"github.com/streadway/amqp"
)

type Server struct {
	amqpConn *amqp.Connection
	logger   *logrus.Logger
	cfg      *config.Config
}

func NewRestaurantServer(amqpConn *amqp.Connection, logger *logrus.Logger, cfg *config.Config) *Server {
	return &Server{amqpConn: amqpConn, logger: logger, cfg: cfg}
}

func (s *Server) Run() {
	ctx, cancel := context.WithCancel(context.Background())
	rmq := s.cfg.RabbitMQ

	// Status producer
	producer, err := rabbitmq.NewStatusPublisher(s.cfg, s.logger)
	if err != nil {
		s.logger.Errorf("StatusPublisher: %v", err)
		cancel()
	}
	defer producer.CloseChan()
	if err := producer.SetupExchangeAndQueue(
		rmq.Exchange,
		rmq.ProduceSettings.QueueName,
		rmq.ProduceSettings.RoutingKey,
		rmq.ConsumerTag,
	); err != nil {
		s.logger.Errorf("StatusPublisher: %v", err)
	}

	// Consumer
	consumer := rabbitmq.NewOrdersConsumer(s.amqpConn, s.logger, producer)
	go func() {
		err := consumer.StartConsumer(
			rmq.WorkerPoolSize,
			rmq.Exchange,
			rmq.ConsumeSettings.QueueName,
			rmq.ConsumeSettings.RoutingKey,
			rmq.ConsumerTag,
		)
		if err != nil {
			s.logger.Errorf("StartConsumer: %v", err)
			cancel()
		}
	}()

	s.logger.Infof("Running order processing until Ctrl+C")

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, os.Interrupt, syscall.SIGTERM)

	select {
	case v := <-quit:
		s.logger.Errorf("signal.Notify: %v", v)
	case done := <-ctx.Done():
		s.logger.Errorf("ctx.Done: %v", done)
	}
}
