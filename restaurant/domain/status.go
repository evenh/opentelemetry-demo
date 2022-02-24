package domain

type StatusUpdate struct {
	OrderId   string `json:"order_id"`
	NewStatus string `json:"new_status"`
}
