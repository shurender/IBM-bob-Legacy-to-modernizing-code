## Running the Legacy Java/JSP Application

Follow the steps below to run the legacy NBFC loan application locally.

run cmd: mvn tomcat7:run

### Prerequisites

The legacy application requires:

- **Java JDK 8**
- **Apache Maven**
- **Apache Tomcat 7**

### 1. Install Java 8

Install **JDK 8** and verify the installation:

```bash
java -version
javac -version
```

The output should show Java 8 / JDK 1.8.

### 2. Install Apache Maven

Install **Apache Maven** and verify:

```bash
mvn -version
```

Maven is used to build the application using the project's `pom.xml`.

### 3. Install Apache Tomcat 7

Install **Apache Tomcat 7**, which is used as the application server for the legacy JSP/Servlet application.

The project uses the `tomcat7-maven-plugin` to run the application.

### 4. Clone / Open the Project

Navigate to the legacy project directory:

```bash
cd legacy-nbfc-loan-system
```

Verify that the project contains:

```text
pom.xml
```

### 5. Build the Application

Run:

```bash
mvn clean package
```

Maven will compile the Java source code and package the application as a WAR file.

The generated WAR file will be available inside:

```text
target/
```

### 6. Start the Application

Run the Tomcat Maven plugin:

```bash
mvn tomcat7:run
```

This starts the legacy application using Apache Tomcat.

### 7. Open the Application

Once Tomcat has started successfully, open the application in a browser:

```text
http://localhost:8080/
```

If the project is configured with a specific context path, use:

```text
http://localhost:8080/legacy-nbfc-loan-system/
```

### Application Flow

```text
JDK 8
   ↓
Apache Maven
   ↓
pom.xml
   ↓
Compile Java + JSP + Servlets
   ↓
WAR Package
   ↓
Apache Tomcat 7
   ↓
Browser
   ↓
Legacy NBFC Loan System
```

### Technology Stack

| Component          | Legacy Technology     |
| ------------------ | --------------------- |
| Language           | Java 8                |
| Web UI             | JSP + JSTL            |
| Web Layer          | Java Servlets         |
| Database Access    | JDBC                  |
| Database           | SQLite / Apache Derby |
| Build Tool         | Apache Maven          |
| Application Server | Apache Tomcat 7       |
| Packaging          | WAR                   |
