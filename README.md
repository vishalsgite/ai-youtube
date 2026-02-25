# 🛰️ Nexus AI: Multi-Agent Video Research & Consensus Pipeline

**Nexus AI** is an advanced, event-driven microservices ecosystem designed to automate deep research by synthesizing insights from multiple YouTube sources. By leveraging **Apache Kafka** for asynchronous orchestration and **Llama 3.3 (Grok Cloud)** as the cognitive engine, Nexus AI identifies consensus points, validates claims, and generates interactive research reports from diverse video content.

---

## 🏗️ System Architecture

Nexus AI is built on a **Choreography-based Saga Pattern**, decoupling long-running data extraction and AI synthesis tasks to ensure high availability and system resilience.



### Core Microservices:
1.  **Topic Management Service:** The primary orchestrator and API Gateway. It manages request lifecycles and persists final research insights in **PostgreSQL**.
2.  **YouTube Processing Service:** The autonomous "Researcher Agent." It discoveres sources and utilizes **Stealth Scraping** with browser cookies to extract transcripts while bypassing 429 blocks.
3.  **AI Analysis Service:** The "Consensus Brain." It aggregates transcript chunks and utilizes **Llama 3.3** to perform cross-source validation and summary generation.

---

## 🚀 Installation & Setup

### Prerequisites
* **Docker & Docker Compose** (Installed and running)
* **Groq Cloud API Key** (For Llama 3.3 access)
* **YouTube Data API Key** (For metadata extraction)
* **Java 21** (For local development)

### Step 1: Clone the Repository
```bash
git clone [https://github.com/your-username/NexusAI.git](https://github.com/your-username/NexusAI.git)
cd NexusAI
