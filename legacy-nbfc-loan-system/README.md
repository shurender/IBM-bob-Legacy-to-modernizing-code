# NBFC Loan Eligibility System

**Legacy Java Web Application — Version 1.0.0**

A 2008–2012-era monolithic Java enterprise web application built for NBFC
(Non-Banking Finance Corporation) loan eligibility assessment.

Built for **legacy modernization demonstration purposes** — to be analyzed and
migrated to Go by an AI agent.

---

## Technology Stack

| Component     | Technology               |
|---------------|--------------------------|
| Language      | Java 8                   |
| Web Layer     | Java Servlets + JSP      |
| Database      | SQLite via JDBC          |
| Build Tool    | Maven 3.x                |
| Server        | Apache Tomcat (embedded) |
| Frontend      | HTML, CSS, Basic JS      |

---

## Project Structure

```
nbfc-loan-system/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── com/nbfc/loan/
        │       ├── model/
        │       │   ├── LoanApplication.java       ← Main domain JavaBean
        │       │   ├── EligibilityDecision.java   ← Decision result object
        │       │   ├── CreditAssessment.java      ← Credit evaluation result
        │       │   ├── IncomeAssessment.java       ← Income evaluation result
        │       │   └── ValidationResult.java      ← Form validation result
        │       ├── dao/
        │       │   └── LoanApplicationDAO.java    ← JDBC data access
        │       ├── service/
        │       │   ├── LoanService.java            ← Top-level service facade
        │       │   ├── EligibilityService.java     ← Decision orchestrator
        │       │   ├── CreditAssessmentService.java
        │       │   ├── IncomeAssessmentService.java
        │       │   └── AgeAssessmentService.java
        │       ├── servlet/
        │       │   ├── LoanServlet.java            ← Main form handler
        │       │   ├── ApplicationListServlet.java
        │       │   └── ApplicationDetailServlet.java
        │       ├── validator/
        │       │   └── InputValidator.java
        │       └── util/
        │           ├── ApplicationConstants.java
        │           ├── DatabaseConnectionManager.java
        │           ├── DatabaseInitializer.java    ← Context listener
        │           ├── FormatUtil.java
        │           └── ReferenceGenerator.java
        ├── resources/
        │   └── schema.sql                          ← Reference schema
        └── webapp/
            ├── index.jsp                           ← Application form
            ├── css/
            │   └── style.css
            └── WEB-INF/
                ├── web.xml
                └── jsp/
                    ├── result.jsp                  ← Decision result
                    ├── applicationList.jsp
                    ├── applicationDetail.jsp
                    ├── error.jsp
                    ├── error404.jsp
                    └── error500.jsp
```

---

## Prerequisites

- **Java 8** (JDK 1.8+)
- **Maven 3.3+**
- Internet connection (first build downloads dependencies)

Verify your installation:
```bash
java -version
mvn -version
```

---

## Build & Run

### Option 1: Run with Embedded Tomcat (Recommended — One Command)

```bash
cd nbfc-loan-system
mvn tomcat7:run
```

Then open your browser:
```
http://localhost:8080/nbfc/
```

The database (`nbfc_loan.db`) is created automatically in your home directory
on first startup.

---

### Option 2: Build WAR and Deploy to Tomcat

```bash
# Build the WAR file
mvn clean package

# WAR is generated at:
target/nbfc-loan-system.war

# Copy to Tomcat webapps directory
cp target/nbfc-loan-system.war /path/to/tomcat/webapps/

# Start Tomcat
/path/to/tomcat/bin/startup.sh

# Access at:
http://localhost:8080/nbfc-loan-system/
```

---

## Application URLs

| URL                              | Description              |
|----------------------------------|--------------------------|
| `http://localhost:8080/nbfc/`    | Loan eligibility form    |
| `http://localhost:8080/nbfc/applications` | All submitted applications |
| `http://localhost:8080/nbfc/application/detail?id=N` | Detail for application ID N |

---

## Eligibility Rules (Business Logic)

### Credit Score Rules
| Score Range | Decision         |
|-------------|------------------|
| 700 – 900   | PASS → eligible  |
| 650 – 699   | REVIEW required  |
| 300 – 649   | FAIL → rejected  |

### Age Rules
| Age         | Decision         |
|-------------|------------------|
| 21 – 60     | PASS             |
| < 21 or >60 | FAIL → rejected  |

### Income Rules
| Monthly Income       | Decision         |
|----------------------|------------------|
| ≥ ₹30,000            | PASS             |
| ₹25,000 – ₹29,999   | REVIEW           |
| < ₹25,000            | FAIL → rejected  |

### Debt-to-Income Ratio Rules (total EMI / monthly income)
| DTI Ratio   | Decision         |
|-------------|------------------|
| < 40%       | PASS             |
| 40% – 49%   | REVIEW           |
| ≥ 50%       | FAIL → rejected  |

### Loan-to-Annual-Income Ratio Rules
| Loan / Annual Income | Decision       |
|----------------------|----------------|
| ≤ 8x                 | PASS           |
| 8x – 10x             | REVIEW         |
| > 10x                | FAIL           |

### Final Decision Hierarchy
1. Any FAIL → **NOT ELIGIBLE**
2. No FAIL but any REVIEW → **MORE INFORMATION REQUIRED**
3. All PASS → **ELIGIBLE**

---

## Test Scenarios (Deterministic)

### Test 1 — ELIGIBLE ✓

| Field            | Value           |
|------------------|-----------------|
| Customer Name    | Rahul Sharma    |
| Age              | 35              |
| Employment Type  | Full Time       |
| Monthly Income   | 70000           |
| Credit Score     | 750             |
| Existing EMI     | 5000            |
| Loan Amount      | 500000          |
| Loan Tenure      | 60 months       |
| **Expected**     | **ELIGIBLE**    |

---

### Test 2 — NOT ELIGIBLE ✗

| Field            | Value           |
|------------------|-----------------|
| Customer Name    | Amit Kumar      |
| Age              | 28              |
| Employment Type  | Full Time       |
| Monthly Income   | 18000           |
| Credit Score     | 520             |
| Existing EMI     | 0               |
| Loan Amount      | 500000          |
| Loan Tenure      | 36 months       |
| **Expected**     | **NOT ELIGIBLE** |

Failure reasons: credit score < 650, income < ₹25,000.

---

### Test 3 — MORE INFORMATION REQUIRED ⚠

| Field            | Value           |
|------------------|-----------------|
| Customer Name    | Priya Singh     |
| Age              | 40              |
| Employment Type  | Full Time       |
| Monthly Income   | 28000           |
| Credit Score     | 675             |
| Existing EMI     | 8000            |
| Loan Amount      | 900000          |
| Loan Tenure      | 120 months      |
| **Expected**     | **REVIEW REQUIRED** |

Review flags: borderline credit score (675), borderline income, high loan-to-income ratio.

---

## Database

SQLite database file location:
```
~/nbfc_loan.db
(your home directory)
```

Tables:
- `loan_applications` — all submitted applications with decisions
- `eligibility_rules` — reference data for eligibility parameters

To inspect the database:
```bash
sqlite3 ~/nbfc_loan.db

.tables
SELECT * FROM loan_applications;
SELECT * FROM eligibility_rules;
.quit
```

---

## Application Flow

```
Browser
   |
   | HTTP POST /nbfc/loan
   v
LoanServlet
   |
   | validates input
   v
InputValidator → ValidationResult
   |
   | creates LoanApplication bean
   v
LoanService.processApplication()
   |
   +---> EligibilityService.evaluate()
   |          |
   |          +---> AgeAssessmentService.assess()
   |          +---> CreditAssessmentService.assess()
   |          +---> IncomeAssessmentService.assess()
   |          |
   |          +---> EligibilityDecision
   |
   +---> LoanApplicationDAO.save()   (SQLite INSERT)
   |
   v
LoanServlet forwards to result.jsp
   |
   v
Browser displays ELIGIBLE / NOT ELIGIBLE / REVIEW REQUIRED
```

---

## Modernization Notes (for AI Agent)

This application is intended to be analyzed and migrated to Go.

Key business rules are in:
- `EligibilityService.java` — decision logic
- `CreditAssessmentService.java` — credit rules
- `IncomeAssessmentService.java` — income/DTI rules
- `AgeAssessmentService.java` — age rules
- `ApplicationConstants.java` — all threshold values

The migrated Go application should produce identical decisions for identical inputs.

---

*NBFC Loan Eligibility System — Version 1.0.0 — Legacy Modernization Demo*
