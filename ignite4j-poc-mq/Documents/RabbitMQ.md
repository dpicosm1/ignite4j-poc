# RabbitMQ PoC

by Ignite Team 2020

## Visión General
**Ignite-4j-poc** es un proyecto de prueba de concepto que tiene como objetivo explorar las capacidades de **RabbitMQ** como Message Broker (agente de mensajería), así como sus `posibilidades de integración` con otros componentes.
El proyecto está desarrollado íntegramente en Java y gestionado con Apache Maven. Los módulos que lo componen son:

- **ignite4j-poc-mq-commons**: librería de utilidades común a todos los proyectos.
- **ignite4j-poc-mq-simple**: implementación de una cola de simple con un productor y un consumidor.
- **ignite4j-poc-mq-taskqueue**: implementación de una cola de tareas con un productor y N-workers.
- **ignite4j-poc-mq-pubsub**: implementación de una cola de subscripción con un publicador y N-subscriptores.