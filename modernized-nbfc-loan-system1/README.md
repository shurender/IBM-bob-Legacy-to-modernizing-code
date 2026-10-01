# Modernized NBFC Loan Eligibility System

run cmd : .\.toolchains\go\bin\go.exe run ./cmd/server

Modern full-stack version of the legacy Java 8 Servlet/JSP/JDBC application in
`../legacy-nbfc-loan-system`.

The Java source remains untouched. The Go services in this folder intentionally
port the Java business logic as the source of truth:

- `ApplicationConstants.java` -> `internal/loan/constants.go`
- `InputValidator.java` -> `internal/loan/validator.go`
- `AgeAssessmentService.java`, `CreditAssessmentService.java`,
  `IncomeAssessmentService.java`, `EligibilityService.java`, `LoanService.java`
  -> `internal/loan/services.go`
- `LoanApplicationDAO.java` and `DatabaseInitializer.java` ->
  `internal/store/sqlite.go`

## Run

Install frontend dependencies and build React:

```powershell
cd frontend
cmd /c npm install
cmd /c npm run build
cd ..
```

Run the Go backend:

```powershell
.\.toolchains\go\bin\go.exe run ./cmd/server
```

Open:

```text
http://localhost:8081
```

The React + TypeScript frontend lives in `frontend` and builds to
`frontend/dist`. The Go backend serves that built React app and exposes the JSON
API under `/api`.

For frontend-only development, run the Go backend in one terminal and then:

```powershell
cd frontend
cmd /c npm run dev
```

Vite proxies `/api` requests to `http://127.0.0.1:8081`.

The SQLite file defaults to:

```text
modernized-nbfc-loan-system/data/nbfc_loan.db
```

Set `NBFC_DB_PATH` to use a different database path.

## API

- `POST /api/applications` validates, evaluates, persists, and reads back a
  saved SQL record.
- `POST /api/eligibility/check` validates and evaluates without persistence.
- `GET /api/applications` returns all applications ordered by `created_at DESC`.
- `GET /api/applications/{id}` returns one application.
- `GET /api/rules` returns the seeded reference rules.

Request payload fields are strings to preserve the original servlet behavior:

```json
{
  "customerName": "Rahul Sharma",
  "age": "35",
  "employmentType": "FULL_TIME",
  "monthlyIncome": "70000",
  "creditScore": "750",
  "existingEmi": "5000",
  "loanAmount": "500000",
  "loanTenure": "60"
}
```

## Verification

Run the Go tests:

```powershell
.\.toolchains\go\bin\go.exe test ./...
```

Run the Java contract check against the original source:

```powershell
javac -cp "../nbfc-loan-system/src/main/java" verification/LegacyContractCheck.java
java -cp ".;../nbfc-loan-system/src/main/java" verification.LegacyContractCheck
```

The README in the legacy project says the Priya Singh sample is review, but the
actual Java code rejects it because the calculated post-loan debt-to-income ratio
is about 74.7%, which is above the hard-fail 50% threshold. This modernized port
follows the Java code.
