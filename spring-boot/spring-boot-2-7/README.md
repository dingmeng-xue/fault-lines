# Spring Boot 2.7 Sample Application

A Spring Boot 2.7.18 application demonstrating features that require changes when migrating to Spring Boot 3.0.

## Breaking Changes

### 1. **javax.* → jakarta.* Package Rename**
- [User.java](src/main/java/com/example/legacy/model/User.java#L7-L10) - `javax.persistence.*`, `javax.validation.*`
- [Product.java](src/main/java/com/example/legacy/model/Product.java#L7-L9) - `javax.persistence.*`, `javax.validation.*`
- [UserDto.java](src/main/java/com/example/legacy/dto/UserDto.java#L5-L7) - `javax.validation.*`
- [UserController.java](src/main/java/com/example/legacy/controller/UserController.java#L10) - `javax.validation.Valid`
- [ProductController.java](src/main/java/com/example/legacy/controller/ProductController.java#L9-L10) - `javax.servlet.*`, `javax.validation.*`

### 2. **WebSecurityConfigurerAdapter Removal**
- [SecurityConfig.java](src/main/java/com/example/legacy/config/SecurityConfig.java#L18) - Extends deprecated `WebSecurityConfigurerAdapter`
- **Fix:** Replace with `SecurityFilterChain` bean pattern

### 3. **Deprecated Actuator Properties**
- [application.properties](src/main/resources/application.properties#L29) - Deprecated `management.endpoints.web.base-path` format

### 4. **Java 11 → Java 17 Requirement**
- [pom.xml](pom.xml#L21-L23) - Java version set to 11

## Quick Start

**Prerequisites:** Java 11+, Maven 3.6+

```bash
# Build and run
mvn spring-boot:run

# Access
# - App: http://localhost:8080
# - H2: http://localhost:8080/h2-console
# - Actuator: http://localhost:8080/actuator
```

**Test Credentials:**
- Public: No auth required for `/api/public/*`
- User: `user` / `password`
- Admin: `admin` / `admin`

**Verify Endpoints:**
```bash
curl http://localhost:8080/api/public/health
# {"status":"UP","version":"2.7.18","springBoot":"2.7.x"}

curl http://localhost:8080/api/public/info
# {"application":"Legacy Spring Boot App","description":"Sample app with Spring Boot 2.7 features that break in 3.0"...}

curl http://localhost:8080/actuator/health
# {"status":"UP","components":{"db":{"status":"UP"},...}}
```

## Migration Checklist

- [ ] Update Spring Boot 2.7 → 3.x in [pom.xml](pom.xml#L12)
- [ ] Update Java 11 → 17 in [pom.xml](pom.xml#L21-L23)
- [ ] Replace `javax.*` → `jakarta.*` imports (5 files)
- [ ] Refactor [SecurityConfig.java](src/main/java/com/example/legacy/config/SecurityConfig.java) to use `SecurityFilterChain`
- [ ] Update [application.properties](src/main/resources/application.properties#L29)
- [ ] Review URL matching and trailing slash behavior changes
- [ ] Test all endpoints after migration
- [ ] Update any third-party dependencies that still use javax.*

## Key Files to Examine

| File | Breaking Change |
|------|----------------|
| `SecurityConfig.java` | WebSecurityConfigurerAdapter |
| `User.java`, `Product.java` | javax.persistence.* |
| `UserDto.java` | javax.validation.* |
| `ProductController.java` | javax.servlet.* |
| `application.properties` | Deprecated properties |
| `pom.xml` | Java version, Spring Boot version |

## Purpose

This application is designed to test migration tools by providing realistic examples of common patterns that break when upgrading from Spring Boot 2.7 to 3.0.
