module github.com/evenh/opentelemetry-demo/restaurant

go 1.18

replace github.com/streadway/amqp v1.2.4 => github.com/rabbitmq/amqp091-go v1.3.0

require (
	github.com/pkg/errors v0.8.0
	github.com/satori/go.uuid v1.2.0
	github.com/sirupsen/logrus v1.8.1
	github.com/streadway/amqp v0.0.0-20180806233856-70e15c650864
	go.opentelemetry.io/contrib/propagators/b3 v1.4.0
	go.opentelemetry.io/otel v1.4.1
	go.opentelemetry.io/otel/exporters/jaeger v1.4.1
	go.opentelemetry.io/otel/sdk v1.4.1
	go.opentelemetry.io/otel/trace v1.4.1
)

require (
	github.com/go-logr/logr v1.2.2 // indirect
	github.com/go-logr/stdr v1.2.2 // indirect
	github.com/kr/text v0.2.0 // indirect
	github.com/niemeyer/pretty v0.0.0-20200227124842-a10e7caefd8e // indirect
	github.com/stretchr/objx v0.3.0 // indirect
	golang.org/x/sys v0.0.0-20210423185535-09eb48e85fd7 // indirect
	gopkg.in/check.v1 v1.0.0-20200227125254-8fa46927fb4f // indirect
)
