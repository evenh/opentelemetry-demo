package domain

import (
	"time"

	uuid "github.com/satori/go.uuid"
)

type Order struct {
	Id         uuid.UUID   `json:"id"`
	Customer   Customer    `json:"customer"`
	OrderLines []OrderLine `json:"orderLines"`
	OrderedAt  time.Time   `json:"orderedAt"`
	Status     string      `json:"status"`
}

type Customer struct {
	FirstName string `json:"firstName"`
	LastName  string `json:"lastName"`
	Address   string `json:"address"`
}

type OrderLine struct {
	Id       uuid.UUID `json:"id"`
	MenuItem MenuItem  `json:"menuItem"`
	Count    int32     `json:"count"`
}

type MenuItem struct {
	Id   uuid.UUID `json:"id"`
	Name string    `json:"name"`
}

func (o *Order) WithStatus(newStatus string) *StatusUpdate {
	return &StatusUpdate{
		OrderId:   o.Id.String(),
		NewStatus: newStatus,
	}
}
