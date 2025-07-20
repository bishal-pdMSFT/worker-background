# Architecture: Supporter.ai Worker Background Service

## Overview
This service automates support ticket and payment transaction analysis using LLMs (OpenAI GPT-4o). It is designed for scalability, extensibility, and robust automation in a modern support workflow.

## Key Modules
- **TicketService / PaymentService:** Ingest tickets and transactions from CSV (strategy pattern for extensibility)
- **LlmService:** Integrates with OpenAI for ticket classification and response generation
- **TicketResponseService:** Generates customer responses based on ticket analysis and recent transactions
- **BusinessOrchestrator:** Orchestrates the async pipeline using in-memory queues
- **Queue DTOs:** `TicketMessage`, `AnalyzedTicketMessage` for message passing

## End-to-End Flow
1. **Ticket Ingestion:** Reads open tickets, puts them on the `incoming-tickets` queue
2. **Analysis Stage:** Consumes tickets, runs LLM analysis, puts `(ticket, analysis)` on `analyzed-tickets` queue
3. **Response Stage:** Consumes analyzed tickets, generates responses, outputs/prints result

## Asynchronous Pipeline Design
- Uses `LinkedBlockingQueue` for in-memory message passing
- Each stage runs in its own thread (ExecutorService)
- Easily swappable for Kafka/RabbitMQ in production

## Extensibility Points
- **Data Sources:** Add new TicketSource/TransactionSource implementations (DB, API, etc.)
- **LLM Service:** Swap out OpenAI for other LLMs or local models
- **Message Broker:** Replace in-memory queues with Kafka, RabbitMQ, etc.

## ASCII Architecture Diagram
```
+-------------------+      +-------------------+      +-------------------+
|  Ticket Producer  | ---> |   LLM Analyzer    | ---> |   Responder/      |
| (TicketService)   |      |  (LlmService)     |      | TicketResponseSvc |
+-------------------+      +-------------------+      +-------------------+
        |                        |                          |
        v                        v                          v
  [incoming-tickets]      [analyzed-tickets]           [output/print]
   (BlockingQueue)         (BlockingQueue)
```

## Scaling & Error Handling
- **Scaling:** Each stage can be scaled independently (thread pool, distributed workers)
- **Error Handling:** Robust try/catch in each stage; failed messages can be retried or sent to a dead-letter queue
- **LLM Quota/Timeouts:** Handled gracefully; fallback to mock/test mode in tests

## Future Improvements
- Pluggable message broker (Kafka, RabbitMQ)
- Persistent storage for tickets/transactions
- Distributed deployment (Kubernetes)
- Advanced monitoring and tracing
- More granular error handling and retries 