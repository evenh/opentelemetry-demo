# opentelemetry-demo

Faggruppemøte i Rett i prod (Bekk)  – 28.02.22. [Slides](/slides.pdf).

## Hvordan kjøre opp apper?

1. Spinn opp `rabbitmq` og `postgres` med docker-compose: `docker-compose up -d rabbitmq postgres`
2. Vent til de har kommet opp, og kjør opp resten: `docker-compose up -d`
3. Se på logger med `docker-compose logs --follow`

Databasen skal nå være seedet med dummydata, AMQP-køer skal være deklarert og konnektivitet på tvers med shipping til Jaeger skal være på plass.

## Hvordan teste tracing?

1. Gjør et kall mot `http://localhost:8080/restaurants`. Noter deg `id` for restauranten du vil bestille fra og id'er til ting på menyen du ønsker å bestille.
2. Fyr av en request på følgende format:
```shell
## Bytt ut id's med de du har fått
curl -X "POST" "http://localhost:8080/orders" \
     -H 'Content-Type: application/json; charset=utf-8' \
     -d $'{
  "orderLines": [
    {
      "count": 1,
      "menuItemRef": "169603f8-1c0b-4946-8667-3cfbcd8ca07c"
    },
    {
      "count": 1,
      "menuItemRef": "0b3f394a-facc-4617-b121-5333b5b936b9"
    }
  ],
  "restaurantId": "69ef07c7-69b7-43ad-bdce-b3e91703b613",
  "customer": {
    "firstName": "Olavsnurr",
    "lastName": "Folkestad",
    "address": "Kammer 39, 0150 Oslo"
  }
}'
```
3. Se i loggen etter trace-id's og spor de opp i Jaeger på http://localhost:16686/search
