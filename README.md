# Supporter.ai Worker Background Service

## Overview
This is a Java Spring Boot background worker for support ticket and payment transaction analysis, powered by LLM (OpenAI GPT-4o). It ingests support tickets and payment transactions, classifies and analyzes them using LLM, and generates automated responses. The pipeline is fully asynchronous and scalable using in-memory message queues.

## Features
- Reads support tickets and payment transactions from CSV (classpath resource)
- Extensible ingestion via strategy pattern (file, DB, etc.)
- LLM-powered ticket analysis and response generation
- Asynchronous, queue-driven pipeline (in-memory, easy to swap for Kafka/RabbitMQ)
- Robust CSV and JSON parsing (OpenCSV, Jackson)
- Dockerized for easy deployment
- Comprehensive unit tests with LLM API mocking

## Tech Stack
- Java 17 LTS, Spring Boot 3.4.x
- Maven (build, plugins: Checkstyle, SpotBugs, JaCoCo, OWASP)
- OpenCSV, Jackson, com.theokanning.openai-gpt3-java
- Docker (Eclipse Temurin base image)
- In-memory message queues (LinkedBlockingQueue)

## Setup & Local Development

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker (for containerized run)
- OpenAI API key (set in `application.properties`)

### Build & Run Locally
```sh
mvn clean package
java -jar target/worker-background-*.jar
```

### Run in Docker
```sh
docker build -t worker-background .
docker run --rm worker-background
```

### Run Tests
```sh
mvn test
```

### Debugging Locally
- **SSL/CA Issues:** If running in Docker behind a proxy (e.g., Zscaler), ensure the root CA is added to the container and Java trust store (see Dockerfile for example).
- **LLM API Errors:** Check your OpenAI API key and quota. Use a mock for tests.
- **CSV Issues:** Ensure test CSVs are in `src/main/resources`.

## Contributing
- Fork and clone the repo
- Create a feature branch (`git checkout -b feature/your-feature`)
- Follow code style (Checkstyle enforced)
- Add/maintain unit tests
- Submit a pull request with a clear description

## Contact & Support
- For issues, open a GitHub issue or contact the maintainer.
- Contributions and suggestions are welcome! 