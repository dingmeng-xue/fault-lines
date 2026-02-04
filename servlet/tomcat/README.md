# Legacy Servlet/Tomcat Application

A traditional Java web application using Servlet 3.1 API, JSP views, and Tomcat container deployment patterns.

## Breaking Changes for Spring Boot Migration

### 1. **WAR → JAR Packaging**
- [pom.xml](pom.xml#L6) - WAR packaging for external Tomcat
- **Fix:** Change to JAR packaging with embedded Tomcat for Spring Boot

### 2. **JSP View Limitations**
- [login.jsp](src/main/webapp/WEB-INF/jsp/login.jsp) - JSP in `/WEB-INF/jsp/`
- [dashboard.jsp](src/main/webapp/WEB-INF/jsp/dashboard.jsp) - Session-based views
- [error pages](src/main/webapp/WEB-INF/jsp/error/) - JSP error handlers
- **Fix:** Migrate to Thymeleaf or build REST API with separate frontend

### 3. **JNDI DataSource to Spring Bean**
- [context.xml](src/main/webapp/META-INF/context.xml#L8-L18) - JNDI DataSource configuration
- [DataExportServlet.java](src/main/java/com/example/legacy/servlet/DataExportServlet.java#L63-L66) - JNDI lookup pattern
- **Fix:** Replace with Spring `@Bean` or `application.properties` configuration

### 4. **web.xml to Java Configuration**
- [web.xml](src/main/webapp/WEB-INF/web.xml) - 180+ lines of XML configuration
  - Servlets: [LoginServlet](src/main/webapp/WEB-INF/web.xml#L56-L62), [UserServlet](src/main/webapp/WEB-INF/web.xml#L66-L72), [ProductServlet](src/main/webapp/WEB-INF/web.xml#L76-L82)
  - Filters: [SecurityFilter](src/main/webapp/WEB-INF/web.xml#L37-L47), [RequestLoggingFilter](src/main/webapp/WEB-INF/web.xml#L50-L54)
  - Context params: [hardcoded config](src/main/webapp/WEB-INF/web.xml#L9-L20)
- **Fix:** Convert to Spring `@Controller`, `@Component`, and `application.properties`

### 5. **javax.* → jakarta.* Namespace**
- [LoginServlet.java](src/main/java/com/example/legacy/servlet/LoginServlet.java#L5-L9) - `javax.servlet.*`
- [UserServlet.java](src/main/java/com/example/legacy/servlet/UserServlet.java#L5-L10) - `javax.servlet.*`
- [ProductServlet.java](src/main/java/com/example/legacy/servlet/ProductServlet.java#L5-L8) - `javax.servlet.*`
- [DataExportServlet.java](src/main/java/com/example/legacy/servlet/DataExportServlet.java#L5-L16) - `javax.servlet.*`, `javax.sql.*`, `javax.naming.*`
- All filter classes: `javax.servlet.*`
- **Fix:** Replace all `javax.*` imports with `jakarta.*` for Spring Boot 3.0+

### 6. **Manual Security to Spring Security**
- [SecurityFilter.java](src/main/java/com/example/legacy/servlet/filter/SecurityFilter.java#L24-L90) - Manual session-based authentication
- [LoginServlet.java](src/main/java/com/example/legacy/servlet/LoginServlet.java#L55-L85) - Hardcoded authentication logic
- **Fix:** Replace with Spring Security `SecurityFilterChain` configuration

### 7. **ServletContextListener to Spring Lifecycle**
- [ApplicationStartupListener.java](src/main/java/com/example/legacy/servlet/listener/ApplicationStartupListener.java#L34-L80) - `contextInitialized()` with JNDI lookups
- [ApplicationStartupListener.java](src/main/java/com/example/legacy/servlet/listener/ApplicationStartupListener.java#L84-L108) - `contextDestroyed()` cleanup
- **Fix:** Use `@PostConstruct`, `CommandLineRunner`, or `@PreDestroy` in Spring

### 8. **HttpSession to Stateless Architecture**
- [LoginServlet.java](src/main/java/com/example/legacy/servlet/LoginServlet.java#L48-L79) - Session-based user state
- [UserServlet.java](src/main/java/com/example/legacy/servlet/UserServlet.java#L155-L159) - Session authorization checks
- [web.xml](src/main/webapp/WEB-INF/web.xml#L23-L31) - Session configuration
- **Fix:** Migrate to JWT tokens or Spring Security context

### 9. **Static Assets Location**
- [style.css](src/main/webapp/css/style.css) - `/css/` directory
- [app.js](src/main/webapp/js/app.js) - `/js/` directory
- **Fix:** Move to `/src/main/resources/static/` for Spring Boot

### 10. **Manual Dependency Management**
- [pom.xml](pom.xml#L24-L66) - Manual versions for commons-lang 2.6, org.json, Log4j 1.2
- **Fix:** Replace with Spring Boot starters and managed dependencies

### 11. **Hardcoded Configuration**
- [LoginServlet.java](src/main/java/com/example/legacy/servlet/LoginServlet.java#L27-L28) - Hardcoded timeout and max attempts
- [web.xml](src/main/webapp/WEB-INF/web.xml#L9-L20) - Context parameters
- **Fix:** Externalize to `application.properties` with `@Value` or `@ConfigurationProperties`

### 12. **Log4j to Spring Boot Logging**
- [log4j.properties](src/main/webapp/WEB-INF/log4j.properties) - Log4j 1.x configuration
- [ApplicationStartupListener.java](src/main/java/com/example/legacy/servlet/listener/ApplicationStartupListener.java#L115-L124) - Manual Log4j setup
- **Fix:** Use Spring Boot's default Logback configuration

## Quick Start

**Prerequisites:** JDK 8+, Apache Tomcat 8.5+/9.x, Maven 3.6+

```bash
# Build WAR
mvn clean package

# Deploy to Tomcat (or copy target/legacy-servlet-app.war to $CATALINA_HOME/webapps/)
mvn tomcat7:deploy

# Access
# - Home: http://localhost:8080/legacy-servlet-app/
# - Login: http://localhost:8080/legacy-servlet-app/login
# - H2 Console: Configured via JNDI in context.xml
```

**Test Credentials:**
- Admin: `admin` / `admin123`
- User: `user` / `user123`

**Verify Endpoints:**
```bash
curl http://localhost:8080/legacy-servlet-app/users/list
# [{"username":"admin","fullName":"Administrator",...},...]

curl http://localhost:8080/legacy-servlet-app/products/list
# {"totalElements":5,"totalPages":1,"currentPage":0,...}

curl http://localhost:8080/legacy-servlet-app/export?type=csv
# Downloads users.csv file
```

## 📊 Migration Effort Estimation

| Category | Complexity | Effort | Priority |
|----------|-----------|--------|----------|
| UMigration Checklist

- [ ] Update packaging to JAR in [pom.xml](pom.xml#L6)
- [ ] Replace JSPs with Thymeleaf or REST API ([5 JSP files](src/main/webapp/WEB-INF/jsp/))
- [ ] Convert JNDI DataSource to Spring Bean ([context.xml](src/main/webapp/META-INF/context.xml), [DataExportServlet](src/main/java/com/example/legacy/servlet/DataExportServlet.java))
- [ ] Convert [web.xml](src/main/webapp/WEB-INF/web.xml) to Java configuration (4 servlets, 3 filters, 1 listener)
- [ ] Replace `javax.*` → `jakarta.*` imports (all servlet/filter/listener classes)
- [ ] Refactor security to Spring Security ([SecurityFilter](src/main/java/com/example/legacy/servlet/filter/SecurityFilter.java), [LoginServlet](src/main/java/com/example/legacy/servlet/LoginServlet.java))
- [ ] Migrate lifecycle hooks to Spring ([ApplicationStartupListener](src/main/java/com/example/legacy/servlet/listener/ApplicationStartupListener.java))
- [ ] Convert session management to stateless/JWT ([LoginServlet](src/main/java/com/example/legacy/servlet/LoginServlet.java), [UserServlet](src/main/java/com/example/legacy/servlet/UserServlet.java))
- [ ] Move static assets to `/static/` ([css](src/main/webapp/css/), [js](src/main/webapp/js/))
- [ ] Replace manual dependencies with Spring Boot starters ([pom.xml](pom.xml#L24-L66))
- [ ] Externalize configuration ([hardcoded values](src/main/java/com/example/legacy/servlet/LoginServlet.java#L27-L28), [context params](src/main/webapp/WEB-INF/web.xml#L9-L20))
- [ ] Update logging to Logback ([log4j.properties](src/main/webapp/WEB-INF/log4j.properties))

## Key Files to Examine

| File | Breaking Change |
|------|----------------|
| `pom.xml` | WAR packaging, manual dependencies |
| `web.xml` | XML configuration for all components |
| `context.xml` | JNDI resources |
| `LoginServlet.java` | Session management, hardcoded auth |
| `DataExportServlet.java` | JNDI lookup pattern |
| `SecurityFilter.java` | Manual security implementation |
| `ApplicationStartupListener.java` | ServletContextListener lifecycle |
| All JSP files | View technology limitations |
| `log4j.properties` | Legacy logging |