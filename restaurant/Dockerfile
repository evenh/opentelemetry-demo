# syntax=docker/dockerfile:1
FROM golang:1.17 as build
WORKDIR /app
COPY . ./
RUN CGO_ENABLED=0 GOOS=linux go build -o worker .

FROM alpine:latest
RUN apk --no-cache add ca-certificates
WORKDIR /root/
COPY --from=build /app/worker ./
CMD ["./worker"]
