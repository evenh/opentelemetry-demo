package rabbitmq

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/evenh/opentelemetry-demo/restaurant/domain"
	"github.com/evenh/opentelemetry-demo/restaurant/pkg/opentelemetry"
	"github.com/pkg/errors"
	"github.com/sirupsen/logrus"
	"github.com/streadway/amqp"
	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/attribute"
	"go.opentelemetry.io/otel/trace"
)

const (
	exchangeKind       = "topic"
	exchangeDurable    = true
	exchangeAutoDelete = false
	exchangeInternal   = false
	exchangeNoWait     = false

	queueDurable    = true
	queueAutoDelete = false
	queueExclusive  = false
	queueNoWait     = false

	publishMandatory = false
	publishImmediate = false

	prefetchCount  = 1
	prefetchSize   = 0
	prefetchGlobal = false

	consumeAutoAck   = false
	consumeExclusive = false
	consumeNoLocal   = false
	consumeNoWait    = false
)

type OrdersConsumer struct {
	amqpConn *amqp.Connection
	logger   *logrus.Logger
	tracer   trace.Tracer
	producer *StatusPublisher
}

func NewOrdersConsumer(amqpConn *amqp.Connection, logger *logrus.Logger, producer *StatusPublisher) *OrdersConsumer {
	tr := otel.GetTracerProvider().Tracer("restaurant-processor")
	return &OrdersConsumer{amqpConn: amqpConn, logger: logger, tracer: tr, producer: producer}
}

// CreateChannel Consume messages
func (c *OrdersConsumer) CreateChannel(exchangeName, queueName, bindingKey, consumerTag string) (*amqp.Channel, error) {
	ch, err := c.amqpConn.Channel()
	if err != nil {
		return nil, errors.Wrap(err, "Error amqpConn.Channel")
	}

	c.logger.Infof("Declaring exchange: %s", exchangeName)
	err = ch.ExchangeDeclare(
		exchangeName,
		exchangeKind,
		exchangeDurable,
		exchangeAutoDelete,
		exchangeInternal,
		exchangeNoWait,
		nil,
	)
	if err != nil {
		return nil, errors.Wrap(err, "Error ch.ExchangeDeclare")
	}

	queue, err := ch.QueueDeclare(
		queueName,
		queueDurable,
		queueAutoDelete,
		queueExclusive,
		queueNoWait,
		nil,
	)
	if err != nil {
		return nil, errors.Wrap(err, "Error ch.QueueDeclare")
	}

	c.logger.Infof("Declared queue, binding it to exchange: ConsumeQueue: %v, messagesCount: %v, "+
		"consumerCount: %v, exchange: %v, bindingKey: %v",
		queue.Name,
		queue.Messages,
		queue.Consumers,
		exchangeName,
		bindingKey,
	)

	err = ch.QueueBind(
		queue.Name,
		bindingKey,
		exchangeName,
		queueNoWait,
		nil,
	)
	if err != nil {
		return nil, errors.Wrap(err, "Error ch.QueueBind")
	}

	c.logger.Infof("ConsumeQueue bound to exchange, starting to consume from queue, consumerTag: %v", consumerTag)

	err = ch.Qos(
		prefetchCount,  // prefetch count
		prefetchSize,   // prefetch size
		prefetchGlobal, // global
	)
	if err != nil {
		return nil, errors.Wrap(err, "Error  ch.Qos")
	}

	return ch, nil
}

func (c *OrdersConsumer) worker(ctx context.Context, messages <-chan amqp.Delivery) {

	for delivery := range messages {
		amqpCtx := opentelemetry.ExtractAMQPHeaders(ctx, delivery.Headers)
		spanCtx, span := c.tracer.Start(amqpCtx, "RestaurantOrdersConsumer.worker")

		// Deserialize
		var payload domain.Order
		if err := json.Unmarshal(delivery.Body, &payload); err != nil {
			c.logger.Warnf("Could not deserialize Order, nacking and will be dropped: %v", err)
			_ = delivery.Nack(false, false)
		}

		span.SetAttributes(
			attribute.String("customer", fmt.Sprintf("%s %s", payload.Customer.FirstName, payload.Customer.LastName)),
			attribute.String("order-id", fmt.Sprintf("%s", payload.Id)),
		)

		logger := c.logger.WithField("order-id", payload.Id).WithField("trace-id", span.SpanContext().TraceID()).WithField("span-id", span.SpanContext().SpanID())

		// Simulate processing
		if err := delivery.Ack(false); err != nil {
			c.logger.Errorf("failed to ack order: %v", err)
		}

		if err := c.updateStatus(spanCtx, payload.WithStatus("ACCEPTED_BY_RESTAURANT")); err == nil {
			logger.Infof("accepted order")
		}

		time.Sleep(10 * time.Second)

		if err := c.updateStatus(spanCtx, payload.WithStatus("MAKING_FOOD")); err == nil {
			logger.Infof("our chef is now making delicious food")
		}

		time.Sleep(20 * time.Second)

		if err := c.updateStatus(spanCtx, payload.WithStatus("OUT_FOR_DELIVERY")); err == nil {
			logger.Infof("the order is now on it's way by courier")
		}

		time.Sleep(30 * time.Second)

		if err := c.updateStatus(spanCtx, payload.WithStatus("DELIVERED")); err == nil {
			logger.Infof("order is all done – thank you next")
		}
		span.End()
	}

	c.logger.Info("Deliveries channel closed")
}

func (c *OrdersConsumer) updateStatus(ctx context.Context, status *domain.StatusUpdate) error {
	var err error
	bytes, err := json.Marshal(status)
	if err != nil {
		return errors.Wrap(err, "could not serialize status")
	}

	spanCtx, span := c.tracer.Start(ctx, "OrdersConsumer.updateStatus", trace.WithAttributes(
		attribute.String("new-status", status.NewStatus),
		attribute.String("order-id", status.OrderId),
	))

	logger := c.logger.WithField("order-id", status.OrderId).WithField("trace-id", span.SpanContext().TraceID()).WithField("span-id", span.SpanContext().SpanID())

	logger.Debugf("setting status to %s", status.NewStatus)
	err = c.producer.Publish(spanCtx, bytes, "application/json")

	span.End()
	return err
}

// StartConsumer Start new rabbitmq consumer
func (c *OrdersConsumer) StartConsumer(workerPoolSize int, exchange, queueName, bindingKey, consumerTag string) error {
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	ch, err := c.CreateChannel(exchange, queueName, bindingKey, consumerTag)
	if err != nil {
		return errors.Wrap(err, "CreateChannel")
	}
	defer ch.Close()

	deliveries, err := ch.Consume(
		queueName,
		consumerTag,
		consumeAutoAck,
		consumeExclusive,
		consumeNoLocal,
		consumeNoWait,
		nil,
	)
	if err != nil {
		return errors.Wrap(err, "Consume")
	}

	for i := 0; i < workerPoolSize; i++ {
		go c.worker(ctx, deliveries)
	}

	chanErr := <-ch.NotifyClose(make(chan *amqp.Error))
	c.logger.Errorf("ch.NotifyClose: %v", chanErr)
	return chanErr
}
