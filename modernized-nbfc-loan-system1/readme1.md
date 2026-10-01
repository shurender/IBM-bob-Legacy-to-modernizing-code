## Running the Modernized Go/React Application

Follow the steps below to run the modernized NBFC loan application locally.

### Prerequisites

The modernized application requires:

- **Go 1.27**
- **Node.js**
- **npm**

### 1. Install Go

Install **Go 1.27** and verify the installation:

```bash
go version
```

Expected:

```text
go version go1.27.x
```

### 2. Install Node.js and npm

Install **Node.js** and verify:

```bash
node -v
npm -v
```

npm is used to install and manage the React frontend dependencies.

### 3. Open the Modernized Project

Navigate to the project directory:

```bash
cd modernized-nbfc-loan-system
```

The project contains the Go module configuration:

```text
go.mod
```

and the frontend package configuration:

```text
package.json
```

### 4. Install Go Dependencies

Run:

```bash
go mod download
```

This downloads the dependencies defined in `go.mod`.

### 5. Start the Go Backend

Run:

```bash
go run .
```

The Go application starts the REST API server.

The backend is typically available at:

```text
http://localhost:8080
```

### 6. Install Frontend Dependencies

Open a new terminal and navigate to the frontend directory:

```bash
cd frontend
```

Install the required npm packages:

```bash
npm install
```

### 7. Start the React Frontend

Run:

```bash
npm run dev
```

Vite starts the React development server.

The frontend is typically available at:

```text
http://localhost:5173
```

### 8. Open the Application

Open the frontend URL in a browser:

```text
http://localhost:5173
```

The React frontend communicates with the Go backend through REST APIs.

### Application Flow

```text
React + TypeScript
       ↓
     Vite
       ↓
Browser
       ↓
REST API
       ↓
Go Backend
       ↓
database/sql
       ↓
SQLite
```

### Production Build

Build the React frontend:

```bash
npm run build
```

Build the Go backend:

```bash
go build
```

This produces a standalone Go executable that can run without Apache Tomcat or another external application server.

### Legacy vs Modernized Runtime

```text
LEGACY
Java 8
   ↓
Maven
   ↓
WAR
   ↓
Tomcat 7
   ↓
JSP / Servlet Application


MODERNIZED
Go 1.27
   ↓
Go Build
   ↓
Standalone Binary
   ↓
REST API

React + TypeScript
   ↓
Vite
   ↓
SPA
   ↓
REST API
```
