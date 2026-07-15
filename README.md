# Task Manager AI Agent

A production-grade Spring Boot REST API with an integrated multi-agent AI orchestration system. Built with JWT authentication, PostgreSQL, and a Code Review Bot that uses specialized AI agents to analyze GitHub Pull Requests.

---

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Spring Boot API                       │
├──────────────┬──────────────┬───────────────────────────┤
│  Task CRUD   │  AI Agent    │  PR Review Orchestrator   │
│  Controller  │  Controller  │  Controller               │
├──────────────┴──────────────┴───────────────────────────┤
│                    Service Layer                         │
├──────────────┬──────────────┬───────────────────────────┤
│  TaskService │  AgentService│  OrchestratorAgent        │
│              │              │  ├── CodeQualityAgent     │
│              │              │  ├── SecurityAgent        │
│              │              │  └── DocumentationAgent   │
├──────────────┴──────────────┴───────────────────────────┤
│              Repository Layer (JPA)                      │
├─────────────────────────────────────────────────────────┤
│           PostgreSQL (Neon Cloud)                        │
└─────────────────────────────────────────────────────────┘
```

---

## Features

### Task Management API
- Full CRUD REST API (GET, POST, PUT, DELETE)
- JWT Authentication with BCrypt password encoding
- Input validation with custom error messages
- Global exception handling
- Spring Actuator health checks

### AI Task Agent
- Natural language interface to your task database
- ReAct pattern — LLM decides which tool to call
- 4 tools: `get_all_tasks`, `create_task`, `complete_task`, `delete_task`
- Powered by Groq (Llama 3.1)

### Multi-Agent PR Code Review
- Fetches PR diff from GitHub API
- 3 specialized AI agents run **in parallel** using CompletableFuture
    - **CodeQualityAgent** — code quality, design patterns, performance
    - **SecurityAgent** — vulnerabilities, exposed secrets, auth issues
    - **DocumentationAgent** — missing JavaDoc, unclear names, README gaps
- Results combined into a structured review report

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.3, Java 21 |
| Security | Spring Security, JWT |
| Database | PostgreSQL (Neon Cloud) |
| ORM | Spring Data JPA / Hibernate |
| AI | Groq API (Llama 3.1) |
| GitHub Integration | GitHub REST API v3 |
| Concurrency | Java CompletableFuture |
| API Docs | Swagger UI (SpringDoc) |
| Build | Maven |

---

## Getting Started

### Prerequisites
- Java 21
- Maven
- PostgreSQL database (or Neon free tier)
- Groq API key (free at console.groq.com)
- GitHub Personal Access Token

### Environment Variables

```bash
GROQ_API_KEY=your_groq_api_key
DB_PASSWORD=your_database_password
GITHUB_TOKEN=your_github_token
```

### Run Locally

```bash
git clone https://github.com/sailohithkolli/taskmanager-ai-agent.git
cd taskmanager-ai-agent

# Set environment variables
export GROQ_API_KEY=your_key
export DB_PASSWORD=your_password
export GITHUB_TOKEN=your_token

# Run
./mvnw spring-boot:run
```

### API Documentation

Once running, visit:
```
http://localhost:8080/swagger-ui/index.html
```

---

## API Endpoints

### Auth
```
POST /auth/register   — Register a new user, returns JWT token
POST /auth/login      — Login, returns JWT token
```

### Tasks (requires JWT)
```
GET    /tasks         — Get all tasks
POST   /tasks         — Create a task
PUT    /tasks/{id}    — Update a task
DELETE /tasks/{id}    — Delete a task
```

### AI Agent
```
POST /agent/chat      — Natural language interface
```

Example:
```json
{ "message": "create a task called Buy groceries" }
{ "message": "what tasks do I have?" }
{ "message": "complete task 1" }
```

### PR Code Review
```
POST /review          — Review a GitHub PR with multi-agent AI
```

Example:
```json
{
    "repoOwner": "sailohithkolli",
    "repoName": "taskmanager-ai-agent",
    "prNumber": 4
}
```

---

## Multi-Agent Orchestration

The PR review system uses parallel agent execution:

```
User Request
    └── OrchestratorAgent
            ├── Fetches PR diff from GitHub (once)
            ├── [PARALLEL] CodeQualityAgent ──→ Groq LLM
            ├── [PARALLEL] SecurityAgent ──────→ Groq LLM
            └── [PARALLEL] DocumentationAgent → Groq LLM
                    └── Combined Report returned
```

Running agents in parallel reduces response time by ~3x compared to sequential execution.

---

## Project Structure

```
src/main/java/com/sai/taskmanager/
├── TaskmanagerApplication.java
├── Task.java                    # Entity
├── User.java                    # Entity
├── TaskController.java          # REST endpoints
├── TaskService.java             # Business logic
├── TaskRepository.java          # DB access
├── AuthController.java          # Auth endpoints
├── AuthService.java             # Auth logic
├── AgentController.java         # AI agent endpoint
├── AgentService.java            # ReAct agent logic
├── CodeReviewController.java    # PR review endpoint
├── OrchestratorAgent.java       # Coordinates agents
├── CodeQualityAgent.java        # Code quality specialist
├── SecurityAgent.java           # Security specialist
├── DocumentationAgent.java      # Documentation specialist
├── GroqClient.java              # Shared Groq API client
├── JwtService.java              # JWT generation/validation
├── JwtFilter.java               # JWT request filter
├── SecurityConfig.java          # Spring Security config
└── GlobalExceptionHandler.java  # Global error handling
```

---

## Key Design Decisions

- **Constructor injection** over `@Autowired` for testability
- **GroqClient** extracted as a shared helper (DRY principle)
- **CompletableFuture** for parallel agent execution
- **Stateless JWT** for horizontal scalability
- **@ControllerAdvice** for centralized error handling
- **Environment variables** for all secrets — no hardcoded credentials

---

## Health Check

```
GET http://localhost:8080/actuator/health
```

```json
{"status": "UP"}
```