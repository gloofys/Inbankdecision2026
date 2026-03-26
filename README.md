# Inbank Loan Decision Engine

This project is a full-stack solution for the Inbank take-home assignment. It includes a Spring Boot backend with a single REST API endpoint and a React frontend that lets a user immediately see the maximum loan offer the decision engine can approve.

Additional notes about product decisions, assumptions, trade-offs, and possible improvements are documented in [THOUGHTS_AND_ASSUMPTIONS.md](THOUGHTS_AND_ASSUMPTIONS.md).

## Technologies Used

### Backend
- Java 25
- Spring Boot 4
- Gradle

### Frontend
- React 19
- TypeScript
- Vite

## Requirements

### Local development
- Java 25
- Node.js
- npm

### Docker setup
- Docker Desktop
- Docker Compose

## Installation

To install and run the project locally, follow these steps:

1. Clone the repository.
2. Start the backend:
   - Navigate to the `backend` directory.
   - Run `./gradlew bootRun` on macOS/Linux or `gradlew.bat bootRun` on Windows.
3. Start the frontend:
   - Navigate to the `frontend` directory.
   - Run `npm install`.
   - Run `npm run dev`.

The backend runs on port `8080` by default and the frontend runs on Vite's default development port.

## Running with Docker

The project can also be started with Docker Compose.

### Start the application

From the project root, run:

```bash
docker compose up --build
```

### Open the application

After the containers start, open:

```text
http://localhost
```

### What runs in Docker

- `backend` – Spring Boot application
- `frontend` – built React application served with nginx
- `nginx` – reverse proxy routing frontend traffic and `/api` requests

### Stop the application

To stop the containers, press `Ctrl + C`.

To remove the containers, run:

```bash
docker compose down
```

## Endpoints

The backend exposes a single endpoint:

### `POST /api/decision`

The request body must contain the following fields:

- `personalCode`: The applicant's 11-digit personal code.
- `loanAmount`: The requested loan amount.
- `loanPeriod`: The requested loan period in months.

Request example:

```json
{
  "personalCode": "49002010976",
  "loanAmount": 4000,
  "loanPeriod": 24
}
```

Response example:

```json
{
  "approved": true,
  "approvedAmount": 2400,
  "approvedPeriod": 24,
  "errorMessage": null,
  "reason": null
}
```

## Supported Scenarios

The assignment is implemented using hardcoded personal codes to simulate external registry results:

- `49002010965` -> debt
- `49002010976` -> segment 1, credit modifier `100`
- `49002010987` -> segment 2, credit modifier `300`
- `49002010998` -> segment 3, credit modifier `1000`

If a person has debt, no loan is approved.

## Decision Logic

The decision engine follows the scoring formula from the assignment:

`credit score = (credit modifier / loan amount) * loan period`

Rules:

- If the credit score is less than `1`, the loan is not approved.
- If the credit score is greater than or equal to `1`, the loan can be approved.
- The engine returns the maximum sum it can approve for the selected period, not only the requested sum.
- If no valid amount is available for the selected period, the engine checks longer periods until it reaches `60` months.
- If no valid amount is found at all, the API returns a negative decision.

## Constraints

- Minimum loan amount: `2000 EUR`
- Maximum loan amount: `10000 EUR`
- Minimum loan period: `12` months
- Maximum loan period: `60` months

## Error Handling

The following error responses can be returned by the backend:

- `400 Bad Request` for invalid input
- `Personal code is required`
- `Personal code must contain exactly 11 digits`
- `Unsupported personal code`
- `Loan amount must be between 2000 and 10000`
- `Loan period must be between 12 and 60 months`

Business decision failures are returned as a successful API response with `approved: false`, for example:

- `Applicant has debt`
- `No valid loan found`

## Architecture

The solution is split into two applications:

- Backend: Spring Boot REST API with controller, service, DTO, exception handler, and configuration layers.
- Frontend: React single-page app that sends requests to the backend and displays the resulting decision in a simple loan calculator UI.

Core backend classes:

- `DecisionController` handles the `/api/decision` endpoint.
- `DecisionService` contains the decision engine logic.
- `GlobalExceptionHandler` converts validation errors into consistent API responses.

## Notes

Additional notes about product decisions, assumptions, trade-offs, and possible improvements are documented in [THOUGHTS_AND_ASSUMPTIONS.md](THOUGHTS_AND_ASSUMPTIONS.md).
