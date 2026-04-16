# SkillBridge AI — Backend CLAUDE.md

> Spring Boot · PostgreSQL · Claude AI  
> Version 1.0 | 16.04.2026

---

## Project Overview

SkillBridge AI is a modular RESTful backend built with **Spring Boot**, using **PostgreSQL** as the database and **Anthropic Claude API** for AI-powered skill assessment, roadmap generation, and job matching.

---

## Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| Framework | Spring Boot 3.x (Java 17) | Core backend framework |
| Security | Spring Security + JWT | Auth and authorization |
| Database | PostgreSQL 15 | Relational data storage |
| ORM | Spring Data JPA / Hibernate | Entity management |
| Migration | Flyway | Schema versioning |
| AI Integration | Anthropic Claude API | Skill analysis, roadmap, scoring |
| Build Tool | Maven | Dependency management |
| Docs | Swagger / OpenAPI 3.0 | API documentation |

---

## Package Structure

```
com.skillbridge
  ├── config/          → Security, CORS, AI client config
  ├── controller/      → REST endpoints
  ├── service/         → Business logic
  ├── repository/      → JPA repositories
  ├── entity/          → JPA entities (DB tables)
  ├── dto/             → Request / Response DTOs
  ├── security/        → JWT filter, UserDetailsService
  ├── ai/              → Claude API wrapper
  └── exception/       → Global exception handler
```

---

## Database Entities

All entities use **UUID** primary keys and are mapped via `@Entity`.

### User (`users`)
All users (student, mentor, employer) are stored here.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | UUID | ✓ PK | Auto-generated primary key |
| email | VARCHAR(255) | ✓ UNIQUE | User email |
| passwordHash | VARCHAR(255) | ✓ | BCrypt-hashed password |
| fullName | VARCHAR(100) | ✓ | Full name |
| role | ENUM | ✓ | `STUDENT` \| `MENTOR` \| `EMPLOYER` \| `ADMIN` |
| avatarUrl | TEXT | — | Profile image URL |
| bio | TEXT | — | Short bio |
| isEmailVerified | BOOLEAN | ✓ | Default: false |
| isActive | BOOLEAN | ✓ | Default: true |
| createdAt | TIMESTAMP | ✓ | Registration date |
| updatedAt | TIMESTAMP | ✓ | Last updated |

### Skill (`skills`)
Catalog of all skills on the platform.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | UUID | ✓ PK | Primary key |
| title | VARCHAR(100) | ✓ | Skill name (e.g. "Backend Development") |
| category | VARCHAR(50) | ✓ | `FRONTEND` \| `BACKEND` \| `DATA` \| `SECURITY` \| `DEVOPS` \| `DESIGN` \| `PM` |
| description | TEXT | — | Detailed description |
| difficulty | ENUM | ✓ | `BEGINNER` \| `INTERMEDIATE` \| `ADVANCED` |
| iconUrl | TEXT | — | Skill icon |
| isActive | BOOLEAN | ✓ | Default: true |

### UserSkill (`user_skills`)
Many-to-many pivot between users and skills.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | UUID | ✓ PK | Primary key |
| userId | UUID (FK) | ✓ | → users.id |
| skillId | UUID (FK) | ✓ | → skills.id |
| currentLevel | ENUM | ✓ | `BEGINNER` \| `INTERMEDIATE` \| `ADVANCED` |
| aiScore | INTEGER | — | AI score (0–100) |
| aiAnalysis | TEXT | — | Claude API response (JSON text) |
| roadmapJson | TEXT | — | AI-generated roadmap (JSON) |
| assessedAt | TIMESTAMP | — | Assessment date |

### Task (`tasks`)
AI-generated or admin-added tasks per skill.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | UUID | ✓ PK | Primary key |
| skillId | UUID (FK) | ✓ | → skills.id |
| title | VARCHAR(200) | ✓ | Task title |
| description | TEXT | ✓ | Full description |
| expectedOutput | TEXT | ✓ | Expected result / rubric |
| difficulty | ENUM | ✓ | `BEGINNER` \| `INTERMEDIATE` \| `ADVANCED` |
| durationMinutes | INTEGER | — | Estimated completion time |
| isAiGenerated | BOOLEAN | ✓ | Default: false |

### Submission (`submissions`)
User's answer to a task + evaluation.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | UUID | ✓ PK | Primary key |
| userId | UUID (FK) | ✓ | → users.id |
| taskId | UUID (FK) | ✓ | → tasks.id |
| solutionText | TEXT | ✓ | User's answer |
| solutionFileUrl | TEXT | — | File URL (if uploaded) |
| status | ENUM | ✓ | `PENDING` \| `AI_REVIEWED` \| `MENTOR_REVIEWED` \| `APPROVED` \| `REJECTED` |
| aiScore | INTEGER | — | AI score (0–100) |
| aiFeedback | TEXT | — | AI feedback text |
| mentorId | UUID (FK) | — | → users.id (nullable) |
| mentorFeedback | TEXT | — | Mentor review |
| finalScore | INTEGER | — | Final score (AI + mentor) |
| submittedAt | TIMESTAMP | ✓ | Submission date |
| reviewedAt | TIMESTAMP | — | Review date |

### Certificate (`certificates`)
Verified badge issued after successful task completion.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | UUID | ✓ PK | Primary key |
| userId | UUID (FK) | ✓ | → users.id |
| skillId | UUID (FK) | ✓ | → skills.id |
| submissionId | UUID (FK) | ✓ | → submissions.id |
| level | ENUM | ✓ | `BEGINNER` \| `INTERMEDIATE` \| `ADVANCED` |
| verificationCode | VARCHAR(50) | ✓ UNIQUE | Public verification code |
| verifiedBy | UUID (FK) | — | → users.id (nullable, mentor) |
| issuedAt | TIMESTAMP | ✓ | Issue date |

### Job (`jobs`)
Vacancies posted by employers.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | UUID | ✓ PK | Primary key |
| employerId | UUID (FK) | ✓ | → users.id (EMPLOYER role) |
| title | VARCHAR(200) | ✓ | Job title |
| company | VARCHAR(100) | ✓ | Company name |
| description | TEXT | ✓ | Job description |
| requiredSkillsJson | TEXT | ✓ | Required skills (JSON array) |
| minLevel | ENUM | ✓ | `BEGINNER` \| `INTERMEDIATE` \| `ADVANCED` |
| jobType | ENUM | ✓ | `INTERNSHIP` \| `JUNIOR` \| `MID` \| `SENIOR` |
| locationType | ENUM | ✓ | `REMOTE` \| `ONSITE` \| `HYBRID` |
| location | VARCHAR(100) | — | City / country |
| isActive | BOOLEAN | ✓ | Default: true |
| createdAt | TIMESTAMP | ✓ | Creation date |

### JobMatch (`job_matches`)
AI-calculated match result (cached).

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | UUID | ✓ PK | Primary key |
| userId | UUID (FK) | ✓ | → users.id |
| jobId | UUID (FK) | ✓ | → jobs.id |
| matchScore | INTEGER | ✓ | Match percentage (0–100) |
| matchReason | TEXT | — | AI reasoning |
| calculatedAt | TIMESTAMP | ✓ | Calculation date |

---

## REST API Endpoints

All endpoints are prefixed with `/api/v1`. Endpoints requiring JWT are marked 🔒.

### Auth (`/api/v1/auth`) — No token required

| Method | Path | Access | Description |
|--------|------|--------|-------------|
| POST | /register | Public | New user registration |
| POST | /login | Public | Login — returns JWT token |
| POST | /refresh-token | Public | Token refresh |
| POST | /logout | 🔒 Auth | Logout — invalidates token |
| POST | /verify-email | Public | Email verification |
| POST | /forgot-password | Public | Send password reset link |

**Register example:**
```json
// Request
{ "email": "...", "password": "...", "fullName": "...", "role": "STUDENT" }

// Response
{ "token": "eyJ...", "refreshToken": "...", "user": { "id": "...", "email": "...", "role": "STUDENT" } }
```

### Users (`/api/v1/users`) — All require 🔒

| Method | Path | Access | Description |
|--------|------|--------|-------------|
| GET | /me | 🔒 Auth user | View own profile |
| PUT | /me | 🔒 Auth user | Update own profile |
| GET | /{id} | 🔒 Auth user | View user profile |
| GET | / | 🔒 ADMIN | All users (admin) |
| DELETE | /{id} | 🔒 ADMIN | Delete user (soft delete) |

### Skills (`/api/v1/skills`)

| Method | Path | Access | Description |
|--------|------|--------|-------------|
| GET | / | Public | List all skills |
| GET | /{id} | Public | Skill detail |
| POST | / | 🔒 ADMIN | Add new skill |
| PUT | /{id} | 🔒 ADMIN | Update skill |

### AI Assessments (`/api/v1/assessments`) — Core AI endpoints

| Method | Path | Access | Description |
|--------|------|--------|-------------|
| POST | /analyze | 🔒 STUDENT | CV/skill input → AI analysis |
| POST | /roadmap/{skillId} | 🔒 STUDENT | Generate AI roadmap |
| GET | /me | 🔒 STUDENT | Own assessment history |
| GET | /me/skill-wallet | 🔒 STUDENT | Skill wallet — all results |

**Analyze example:**
```json
// Request
{ "skillId": "uuid", "selfRating": 3, "answers": ["...", "..."] }

// Response
{
  "level": "INTERMEDIATE",
  "score": 72,
  "strengths": ["API design", "Database queries"],
  "gaps": ["JWT Auth", "Unit testing"],
  "nextSteps": "Learn JWT and Spring Security...",
  "roadmap": { "weeks": [...] }
}
```

### Tasks (`/api/v1/tasks`)

| Method | Path | Access | Description |
|--------|------|--------|-------------|
| GET | /skill/{skillId} | 🔒 Auth | Tasks by skill |
| GET | /{id} | 🔒 Auth | Task detail |
| POST | /generate/{skillId} | 🔒 STUDENT | Generate AI task |
| POST | / | 🔒 ADMIN/MENTOR | Add manual task |
| PUT | /{id} | 🔒 ADMIN/MENTOR | Update task |

### Submissions (`/api/v1/submissions`)

| Method | Path | Access | Description |
|--------|------|--------|-------------|
| POST | / | 🔒 STUDENT | Submit task answer |
| POST | /{id}/ai-review | 🔒 System | Trigger AI scoring |
| POST | /{id}/mentor-review | 🔒 MENTOR | Mentor review and score |
| GET | /me | 🔒 STUDENT | Own submissions |
| GET | /pending | 🔒 MENTOR | Submissions awaiting review |
| GET | /{id} | 🔒 Auth | Submission detail |

### Certificates (`/api/v1/certificates`)

| Method | Path | Access | Description |
|--------|------|--------|-------------|
| GET | /me | 🔒 STUDENT | Own certificates |
| GET | /user/{userId} | 🔒 Auth | User's certificates |
| GET | /verify/{code} | Public | Verify certificate (public link) |
| GET | /{id} | 🔒 Auth | Certificate detail |

### Jobs (`/api/v1/jobs`)

| Method | Path | Access | Description |
|--------|------|--------|-------------|
| GET | / | Public | All active vacancies |
| GET | /{id} | Public | Job detail |
| POST | / | 🔒 EMPLOYER | Post new job |
| PUT | /{id} | 🔒 EMPLOYER | Update job |
| DELETE | /{id} | 🔒 EMPLOYER | Delete job (soft delete) |
| GET | /me/matches | 🔒 STUDENT | AI match results |
| POST | /{id}/calculate-match | 🔒 STUDENT | Calculate AI match score |
| GET | /employer/candidates | 🔒 EMPLOYER | Matching candidates list |

---

## Security — JWT Authentication

### JWT Configuration (`application.properties`)
```properties
jwt.secret=skillbridge_secret_key_min_256_bit
jwt.expiration=86400000          # 24 hours (ms)
jwt.refresh-expiration=604800000  # 7 days (ms)
```

### Authorization Header
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Payload
```json
{
  "sub": "user-uuid",
  "role": "STUDENT",
  "email": "user@example.com",
  "iat": 1714000000,
  "exp": 1714086400
}
```

### Permission Matrix

| Endpoint | STUDENT | MENTOR | EMPLOYER | ADMIN |
|----------|---------|--------|----------|-------|
| /auth/** | ✓ | ✓ | ✓ | ✓ |
| /skills GET | ✓ | ✓ | ✓ | ✓ |
| /assessments/analyze | ✓ | — | — | ✓ |
| /submissions/mentor-review | — | ✓ | — | ✓ |
| /jobs (POST/PUT/DELETE) | — | — | ✓ | ✓ |
| /users (admin ops) | — | — | — | ✓ |

### CORS Configuration
```java
config.setAllowedOrigins(List.of("http://localhost:3000"));
config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
config.setAllowedHeaders(List.of("*"));
config.setAllowCredentials(true);
```

---

## AI Integration (Claude API)

All AI calls are executed **asynchronously**. Main service class: `AiService.java`

### Maven Dependency
```xml
<dependency>
  <groupId>com.squareup.okhttp3</groupId>
  <artifactId>okhttp</artifactId>
  <version>4.12.0</version>
</dependency>
```

### Configuration
```properties
anthropic.api.key=${ANTHROPIC_API_KEY}
anthropic.api.url=https://api.anthropic.com/v1/messages
anthropic.model=claude-sonnet-4-20250514
```

### AI Service Methods

| Method | Endpoint | Description |
|--------|----------|-------------|
| `analyzeSkill(userId, skillId, answers)` | /analyze | Analyzes user answers, returns level and score |
| `generateRoadmap(userId, skillId)` | /roadmap | Creates a personalized 2-week learning plan |
| `generateTask(skillId, level)` | /generate | Generates a practical task for the given level |
| `reviewSubmission(submissionId)` | /ai-review | Evaluates submission and provides feedback |
| `calculateJobMatch(userId, jobId)` | /calculate-match | Compares user skills against job requirements |

### Example Prompt (Skill Analysis)
```java
String prompt = """
  You are a technical skill evaluator.
  Skill: %s | Answers: %s
  Respond ONLY with this JSON format:
  {
    "level": "BEGINNER|INTERMEDIATE|ADVANCED",
    "score": 0-100,
    "strengths": ["..."],
    "gaps": ["..."],
    "summary": "..."
  }
  Return only JSON, nothing else.
""".formatted(skillTitle, answersJson);
```

---

## Error Handling

All exceptions are caught via `@RestControllerAdvice`. Standard error response format:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found",
  "path": "/api/v1/users/abc"
}
```

### HTTP Status Codes

| Code | Name | Usage |
|------|------|-------|
| 200 | OK | Successful GET, PUT |
| 201 | Created | Successful POST (new resource created) |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Validation error, invalid input |
| 401 | Unauthorized | Missing or invalid token |
| 403 | Forbidden | Insufficient permissions (wrong role) |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Already exists (e.g. email) |
| 500 | Server Error | Internal server error |

---

## Database Indexes

```sql
CREATE INDEX idx_user_skills_user_id ON user_skills(user_id);
CREATE INDEX idx_submissions_user_id ON submissions(user_id);
CREATE INDEX idx_submissions_task_id ON submissions(task_id);
CREATE INDEX idx_submissions_status ON submissions(status);
CREATE INDEX idx_job_matches_user_id ON job_matches(user_id);
CREATE INDEX idx_certificates_user_id ON certificates(user_id);
CREATE UNIQUE INDEX idx_certificates_code ON certificates(verification_code);
```

---

## application.properties Reference

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/skillbridge
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Flyway
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# Anthropic AI
anthropic.api.key=${ANTHROPIC_API_KEY}
anthropic.model=claude-sonnet-4-20250514

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

---

## Entity Relationships

- **User ↔ Skill** — many-to-many (via `UserSkill` pivot table)
- **User → Submission** — one-to-many
- **Task → Submission** — one-to-many
- **Submission → Certificate** — one-to-one (APPROVED submission creates certificate)
- **User (EMPLOYER) → Job** — one-to-many
- **User + Job → JobMatch** — many-to-many (AI match result)

**Cascade rule:** Deleting a User cascades to all `UserSkill`, `Submission`, `Certificate`, `JobMatch` records. Jobs use soft delete (`isActive = false`).

---

## Hackathon MVP Priority (48 hours)

| # | Module | Time | Notes |
|---|--------|------|-------|
| 1 | Auth (register/login/JWT) | 2–3 hrs | Everything depends on this |
| 2 | User profile + Skill catalog | 2 hrs | Simple CRUD, add seed data |
| 3 | AI Skill Assessment | 3–4 hrs | Strongest part of the demo |
| 4 | Task assignment + Submission | 3 hrs | AI review + simple scoring |
| 5 | Certificate / Badge creation | 2 hrs | High visual impact in demo |
| 6 | Job Matching (AI match score) | 3–4 hrs | Last step — high wow factor |

> **Key for judges:** Every step in the demo must show a real API response — not hardcoded data. A live Claude API call is immediately noticeable.
