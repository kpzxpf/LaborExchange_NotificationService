# Notification Service

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.6-brightgreen?logo=springboot)
![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Port](https://img.shields.io/badge/port-8088-blue)
![Kafka](https://img.shields.io/badge/Kafka-consumer-231F20?logo=apachekafka)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

Event-driven email notification microservice. Consumes application lifecycle events from Kafka and sends transactional emails to candidates.

## Table of Contents

- [Overview](#overview)
- [Kafka Events](#kafka-events)
- [Email Templates](#email-templates)
- [Configuration](#configuration)
- [Running Locally](#running-locally)

## Overview

| Property | Value |
|---|---|
| Port | **8088** |
| REST API | None — Kafka consumer only |
| Kafka topic | `notification-events` |
| Email provider | SMTP (Spring Mail) |
| Prometheus | `http://localhost:8088/actuator/prometheus` |

## Kafka Events

The service listens to the `notification-events` topic. Each event carries:

| Field | Type | Description |
|---|---|---|
| `type` | String | `APPLICATION_CREATED`, `APPLICATION_REJECTED`, `APPLICATION_WITHDRAWN` |
| `candidateEmail` | String | Recipient email address |
| `candidateName` | String | Recipient name |
| `vacancyTitle` | String | Job vacancy title |
| `companyName` | String | Employer company name |

Events are published by **ApplicationService** on:
- Application creation → `APPLICATION_CREATED`
- Application rejection → `APPLICATION_REJECTED`
- Application withdrawal → `APPLICATION_WITHDRAWN`

## Email Templates

| Event type | Subject | Content |
|---|---|---|
| `APPLICATION_CREATED` | Application submitted | Confirmation with vacancy title and company name |
| `APPLICATION_REJECTED` | Application update | Notification of rejection |
| `APPLICATION_WITHDRAWN` | Application withdrawn | Confirmation of withdrawal |

## Configuration

| Property | Default | Description |
|---|---|---|
| `server.port` | `8088` | Actuator port |
| `spring.kafka.bootstrap-servers` | `localhost:9092` | Kafka brokers |
| `spring.kafka.consumer.group-id` | `notification-service` | Consumer group |
| `spring.mail.host` | — | SMTP server host |
| `spring.mail.port` | `587` | SMTP port |
| `spring.mail.username` | env var | SMTP username |
| `spring.mail.password` | env var | SMTP password |

## Running Locally

```bash
./gradlew bootRun
```

Requires Kafka running on port 9092 and a reachable SMTP server. ApplicationService must be running to produce events.
