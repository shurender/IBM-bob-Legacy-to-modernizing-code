## Technology Stack

### Legacy System — `legacy-nbfc-loan-system`

- **Language & Runtime:** Java 8 (JDK 1.8)
- **Architecture:** Server-Side Monolith (MVC)
- **Web / HTTP:** Java EE `HttpServlet` (`javax.servlet`)
- **Frontend:** JSP (JavaServer Pages), JSTL, Vanilla CSS
- **Database:** SQLite / Apache Derby
- **Data Access:** JDBC (`java.sql.*`)
- **Build Tool:** Apache Maven (`pom.xml`)
- **Application Server:** Apache Tomcat 7

### Modernized System — `modernized-nbfc-loan-system`

- **Backend:** Go 1.22
- **Frontend:** React 18 + TypeScript
- **Architecture:** Decoupled REST API + Single Page Application (SPA)
- **Web / HTTP:** Go `net/http` with router
- **Database:** SQLite
- **Data Access:** Go `database/sql` + `modernc.org/sqlite`
- **Build & Package Management:** Go Toolchain (`go.mod`) + npm (`package.json`)
- **Frontend Build Tool:** Vite
- **Styling:** Vanilla CSS
- **Deployment:** Standalone compiled Go binary — no external application server required
