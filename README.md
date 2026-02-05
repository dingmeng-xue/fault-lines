# FaultLines

A collection of sample applications demonstrating breaking changes across framework migrations and platform upgrades.

## 🏛️ Structural Integrity Classification

Projects are categorized by their **readiness** and **testability** using a five-tier hierarchy:

| Type | Name | Status | Description | Benchmark Value |
|------|------|--------|-------------|-----------------|
| **A** | **Verified**<br>(Full-Stack) | ✅ Compiles<br>+ Tests Pass | Project builds successfully and includes automated tests (unit/integration) that pass. | **High** - Best for detecting behavioral drift. Can prove breaking changes via "Green to Red" test transitions. |
| **B** | **Documented**<br>(Manual Test Guide) | ⚠️ Compiles<br>+ Manual Test Doc | Project builds and runs with documented manual testing instructions (endpoints, credentials, verification steps). | **Medium-High** - Requires manual verification but provides clear test procedures to demonstrate functionality. |
| **C** | **Blind Build**<br>(Opaque) | ⚠️ Compiles<br>+ No Test Doc | Project builds and runs, but lacks both automated tests and testing documentation. Functionality verification is undocumented. | **Medium** - Requires reverse-engineering or exploratory testing to verify behavior. |
| **D** | **Severed**<br>(Linkage Fail) | ❌ Source Present<br>+ Won't Compile | Source code exists but cannot compile due to missing dependencies, environment issues, or broken build configuration. | **Technical** - The breaking change is often the build environment itself. |
| **E** | **Skeletal**<br>(Source Only) | 📄 Raw Source<br>Files | Just source code without build configuration (no `pom.xml`, `package.json`, `Makefile`, etc.). | **Low/Manual** - Must be hosted in a new project to test. |

## 📂 Projects

| Project | Type |
|---------|------|
| `spring-boot/spring-boot-2-7` | **B** |
| `servlet/tomcat` | **B** |
