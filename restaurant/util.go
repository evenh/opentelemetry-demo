package main

import (
	"fmt"
	"os"
)

// AMQPlainAuth is similar to PlainAuth
type AMQPlainAuth struct {
	Username string
	Password string
}

// Mechanism returns "AMQPLAIN"
func (auth *AMQPlainAuth) Mechanism() string {
	return "AMQPLAIN"
}

// Response returns the null character delimited encoding for the SASL PLAIN Mechanism.
func (auth *AMQPlainAuth) Response() string {
	return fmt.Sprintf("LOGIN:%sPASSWORD:%s", auth.Username, auth.Password)
}

func getEnv(key, defaultValue string) string {
	value := os.Getenv(key)
	if len(value) == 0 {
		return defaultValue
	}
	return value
}
