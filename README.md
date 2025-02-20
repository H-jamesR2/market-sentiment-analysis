# Market Sentiment Analysis - Reddit Data Pipeline

## 🚀 Overview

This project is a **real-time market sentiment analysis** system that ingests and processes data from **Reddit**, particularly **r/wallstreetbets**, **r/fednews**, **r/economics** (for now) to track **market discussions, trends, and sentiment shifts**. The system is designed to **ingest posts and comments**, deduplicate them efficiently using **Redis**, and stream the cleaned data into **Kafka** for further analysis (later on).

## 🏗️ Architecture

- **Ingestion Layer (Spring Boot)**
  - Fetches **Reddit posts & comments** using OAuth authentication.
  - Utilizes **Kafka Producers** to stream data into topics (`reddit_posts`, `reddit_comments`).
  - Implements **Redis-based deduplication** to optimize storage and processing.

- **Processing Layer (Planned)**
  - Real-time **sentiment scoring** (using NLP models or rule-based heuristics).
  - Aggregates upvotes, engagement metrics for sentiment trends.
  - Stores processed data in **Cassandra (planned)** for historical analysis.

- **Visualization & Analytics (Planned)**
  - **REST API** to expose sentiment scores & trends.
  - **Dashboard integration** for visual analytics.

## 🛠️ Tech Stack

| Component | Technology Used |
|-----------|----------------|
| **Backend** | Java (Spring Boot) |
| **Messaging Queue** | Apache Kafka |
| **Deduplication** | Redis |
| **Database (Planned)** | Cassandra |
| **Deployment** | Docker, Kubernetes (future) |

## 📦 Repository Structure

```
market-sentiment-analysis/
│── ingestion/
│   ├── src/main/java/com/example/ingestion/
│   │   ├── fetcher/          # RedditDataFetcher, API Handlers
│   │   ├── messaging/        # KafkaProducerService
│   │   ├── models/           # RedditPost, RedditComment
│   │   ├── parsers/          # RedditParser
│   │   ├── scheduler/        # DataFetchScheduler
│   │   ├── services/         # RedditAuthService, RedisDeduplicationService
│   │   ├── Main.java         # Application entry point
│── backend/ (Planned)
│── resources/
│── docker-compose.yml
│── README.md
```

## 🚀 Getting Started

### Prerequisites

- **Docker** & **Docker Compose**
- **Java 11+** (Spring Boot)
- **Apache Kafka**
- **Redis**

### Local Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/market-sentiment-analysis.git
   cd market-sentiment-analysis
   ```

2. Start Kafka & Redis using Docker:
   ```bash
   docker-compose up -d
   ```

3. Run the ingestion service:
   ```bash
   cd ingestion
   mvn spring-boot:run
   ```

4. Verify Kafka topics:
   ```bash
   docker exec -it <kafka-container> kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic reddit_posts --from-beginning
   ```

## 🛠️ Next Steps

✅ **Ingest posts & comments**  
✅ **Kafka integration**  
✅ **Redis deduplication**  
🔜 **Sentiment scoring**  
🔜 **Cassandra integration**  
🔜 **Dashboard & API for insights**  
