package rabbitmq

import (
	"context"
	"time"

	"github.com/evenh/opentelemetry-demo/restaurant/config"
	"github.com/evenh/opentelemetry-demo/restaurant/pkg/opentelemetry"
	"github.com/pkg/errors"
	uuid "github.com/satori/go.uuid"
	"github.com/sirupsen/logrus"
	"github.com/streadway/amqp"
)

type StatusPublisher struct {
	amqpChan *amqp.Channel
	cfg      *config.Config
	logger   *logrus.Logger
}

func NewStatusPublisher(cfg *config.Config, logger *logrus.Logger) (*StatusPublisher, error) {
	mqConn, err := NewRabbitMQConn(cfg)
	if err != nil {
		return nil, err
	}
	amqpChan, err := mqConn.Channel()
	if err != nil {
		return nil, errors.Wrap(err, "p.amqpConn.Channel")
	}

	return &StatusPublisher{cfg: cfg, logger: logger, amqpChan: amqpChan}, nil
}

// SetupExchangeAndQueue create exchange and queue
func (p *StatusPublisher) SetupExchangeAndQueue(exchange, queueName, bindingKey, consumerTag string) error {
	p.logger.Infof("Declaring exchange: %s", exchange)
	err := p.amqpChan.ExchangeDeclare(
		exchange,
		exchangeKind,
		exchangeDurable,
		exchangeAutoDelete,
		exchangeInternal,
		exchangeNoWait,
		nil,
	)
	if err != nil {
		return errors.Wrap(err, "Error ch.ExchangeDeclare")
	}

	queue, err := p.amqpChan.QueueDeclare(
		queueName,
		queueDurable,
		queueAutoDelete,
		queueExclusive,
		queueNoWait,
		nil,
	)
	if err != nil {
		return errors.Wrap(err, "Error ch.QueueDeclare")
	}

	p.logger.Infof("Declared queue, binding it to exchange: ConsumeQueue: %v, messageCount: %v, "+
		"consumerCount: %v, exchange: %v, exchange: %v, bindingKey: %v",
		queue.Name,
		queue.Messages,
		queue.Consumers,
		exchange,
		bindingKey,
	)

	err = p.amqpChan.QueueBind(
		queue.Name,
		bindingKey,
		exchange,
		queueNoWait,
		nil,
	)
	if err != nil {
		return errors.Wrap(err, "Error ch.QueueBind")
	}

	p.logger.Infof("ConsumeQueue bound to exchange, starting to consume from queue, consumerTag: %v", consumerTag)
	return nil
}

// CloseChan Close messages chan
func (p *StatusPublisher) CloseChan() {
	if err := p.amqpChan.Close(); err != nil {
		p.logger.Errorf("StatusPublisher CloseChan: %v", err)
	}
}

// Publish message
func (p *StatusPublisher) Publish(ctx context.Context, body []byte, contentType string) error {
	if err := p.amqpChan.Publish(
		p.cfg.RabbitMQ.Exchange,
		p.cfg.RabbitMQ.ProduceSettings.RoutingKey,
		publishMandatory,
		publishImmediate,
		amqp.Publishing{
			ContentType:  contentType,
			DeliveryMode: amqp.Persistent,
			MessageId:    uuid.NewV4().String(),
			Timestamp:    time.Now(),
			Body:         body,
			Headers:      opentelemetry.InjectAMQPHeaders(ctx),
		},
	); err != nil {
		return errors.Wrap(err, "ch.Publish")
	}

	return nil
}
