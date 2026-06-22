# CLAUDE.md — Legacy Insurance Platform · Java 8 → 17 Migration

> **Read this first in every session.** This file is the only persistent memory
> across Claude Code sessions. Update the Decision Log and Batch Status sections
> before ending each session, or context is lost.

---

## 1. Project Overview

**Repo:** `legacy-insurance-platform` — multi-module Maven monorepo  
**Goal:** Migrate from Java 8 / `javax.*` to Java 17 / `jakarta.*`  
**Base package:** `com.acme.insurance`  
**Build tool:** Maven 3.x (plain aggregator parent — NOT spring-boot-starter-parent)

### Module Map (dependency order — migrate top to bottom)

```
shared-domain          → JAXB models; everything imports this; migrate FIRST
policy-soap-service    → depends on shared-domain; CXF + JAX-WS
claims-core-service    → depends on shared-domain; Spring Boot + Hibernate + Jasypt + XStream
admin-web              → depends on shared-domain; JSF + DWR (DWR = rewrite, not upgrade)
```

### Key version properties (current baseline — all intentionally old)

| Property | Current (JDK 8) | Target (JDK 17) |
|---|---|---|
| `maven.compiler.source/target` | `1.8` | `17` |
| `spring.boot.version` | `2.7.18` | `3.x` |
| `cxf.version` | `3.5.5` | `4.x` |
| `hibernate.version` | `5.6.15.Final` | `6.x` (or Boot 3 managed) |
| `jaxb.api.version` | `2.3.1` | `4.x` (jakarta) |
| `jaxb.impl.version` (com.sun.xml.bind) | `2.3.3` | replace with `org.glassfish.jaxb:jaxb-runtime:4.x` |
| `jasypt.version` | `1.9.3` | `1.9.x→` not needed; starter drives it |
| `jasypt.spring.boot.version` | `2.1.2` | `3.x` |
| `xstream.version` | `1.4.20` | current (+ security hardening) |
| `mojarra.version` | `2.3.9` | Jakarta Faces 4.x |
| `dwr.version` | `3.0.2-RELEASE` | **no jakarta release — rewrite as REST** |

---

## 2. Workflow Conventions

### Branch naming
```
migrate/java17                   ← integration branch (all batches merge here)
  batch-1/shared-domain
  batch-2/policy-soap
  batch-3/claims-persistence
  batch-4/claims-security
  batch-5/admin-jsf
  batch-6/admin-dwr-rewrite
```

### Commit message format
```
[migrate] batch-N/<module>: <short description of what changed>

Examples:
[migrate] batch-1/shared-domain: javax.xml.bind → jakarta.xml.bind in all models
[migrate] batch-1/shared-domain: pom - swap jaxb-api/jaxb-impl for jakarta+glassfish runtime
```

Every AI-driven commit MUST use the `[migrate]` prefix so changes are filterable:
```
git log --grep="\[migrate\]"
```

### Definition of "done" per batch
1. `mvn clean compile -pl <module> -am` passes on JDK 17
2. No `javax.*` EE imports remain in scope files (run: `grep -r "javax\." src/main/java`)
3. Decision Log updated in this file before session ends
4. PR opened against `migrate/java17` for human review

### What NOT to change in a session
- Do not edit files outside the current batch scope
- Do not merge the batch branch yourself — leave the PR open for review
- Do not delete `MIGRATION-ANSWER-KEY.md` — it is the grading rubric, not source code

---

## 3. Landmine Inventory & Batch Checklist

Mark each item `[ ]` pending → `[~]` in-progress → `[x]` caught → `[!]` partial/missed.

### Batch 1 — shared-domain

**Files in scope:**
- `shared-domain/pom.xml`
- `shared-domain/src/main/java/com/acme/insurance/shared/model/Policy.java`
- `shared-domain/src/main/java/com/acme/insurance/shared/model/Claim.java`
- `shared-domain/src/main/java/com/acme/insurance/shared/model/Party.java`
- `shared-domain/src/main/java/com/acme/insurance/shared/model/ClaimStatus.java`
- `shared-domain/src/main/java/com/acme/insurance/shared/util/LocalDateAdapter.java`
- `shared-domain/src/main/java/com/acme/insurance/shared/xml/JaxbSupport.java`

| # | Landmine | Status | Notes |
|---|----------|--------|-------|
| L1 | JAXB API: `javax.xml.bind.*` → `jakarta.xml.bind.*` in all model + util files | `[x]` | All 7 files migrated |
| L2 | JAXB runtime: add `org.glassfish.jaxb:jaxb-runtime:4.x` (runtime scope) to pom; `JAXBContext.newInstance()` throws at runtime without it even if compile passes | `[x]` | jaxb-runtime:4.0.5 added with runtime scope |

**Context to chain forward after Batch 1:**
- Chosen JAXB API artifact: `jakarta.xml.bind:jakarta.xml.bind-api:4.x`
- Chosen JAXB runtime: `org.glassfish.jaxb:jaxb-runtime:4.x` (runtime scope)
- `LocalDateAdapter` extends `jakarta.xml.bind.annotation.adapters.XmlAdapter`

---

### Batch 2 — policy-soap-service

**Files in scope:**
- `policy-soap-service/pom.xml`
- `policy-soap-service/src/main/java/com/acme/insurance/policy/ws/PolicyService.java`
- `policy-soap-service/src/main/java/com/acme/insurance/policy/ws/PolicyServiceImpl.java`
- `policy-soap-service/src/main/java/com/acme/insurance/policy/ws/PolicyQuoteRequest.java`
- `policy-soap-service/src/main/java/com/acme/insurance/policy/ws/PolicyQuoteResponse.java`
- `policy-soap-service/src/main/java/com/acme/insurance/policy/config/PolicyEndpointPublisher.java`

| # | Landmine | Status | Notes |
|---|----------|--------|-------|
| L3 | CXF bump: `cxf.version` 3.5.x → 4.x in pom | `[x]` | 3.5.5 → 4.0.5 in parent pom |
| L4 | JAX-WS/JWS: `javax.jws.*` → `jakarta.jws.*`; `javax.xml.ws.*` → `jakarta.xml.ws.*` | `[x]` | PolicyService + PolicyServiceImpl migrated; old javax.jws-api/jaxws-api deps removed (CXF 4.x provides transitively) |
| L5 | CXF false-negative: `JaxWsServerFactoryBean` class name unchanged between CXF 3 and 4 — no import edit needed but only correct once L3+L4 are done | `[x]` | Flagged: import unchanged, but now backed by CXF 4.x + jakarta; depends on L3+L4 |
| L6 | JAXB on request/response beans: same `javax→jakarta` move as L1, in this module's beans | `[x]` | PolicyQuoteRequest + PolicyQuoteResponse migrated; jakarta.xml.bind-api added as explicit dep |

**Context to chain forward after Batch 2:**
- CXF is on 4.x; jakarta-linked
- `JaxWsServerFactoryBean` import unchanged but jakarta-backed

---

### Batch 3 — claims-core-service (persistence layer)

**Files in scope:**
- `claims-core-service/pom.xml`
- `claims-core-service/src/main/java/com/acme/insurance/claims/ClaimsApplication.java`
- `claims-core-service/src/main/java/com/acme/insurance/claims/entity/ClaimEntity.java`
- `claims-core-service/src/main/java/com/acme/insurance/claims/repo/ClaimRepository.java`
- `claims-core-service/src/main/resources/application.properties`

| # | Landmine | Status | Notes |
|---|----------|--------|-------|
| L7 | Spring Boot 2→3: bump `spring.boot.version` in parent pom; BOM import (not a parent inheritance — requires changing the BOM import version) | `[x]` | 2.7.18 → 3.2.5 in parent pom |
| L8 | `javax.persistence.*` → `jakarta.persistence.*`; remove explicit `hibernate.version` pin (let Boot 3 manage Hibernate 6) | `[x]` | ClaimEntity migrated; explicit hibernate-core dep removed |
| L9 | `@Type(type="yes_no")` removed in Hibernate 6: replace with `AttributeConverter<Boolean,String>` + `@Convert` | `[x]` | Created YesNoConverter; @Type replaced with @Convert |
| L10 | Hibernate dialect: drop explicit `spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect` from application.properties (let Boot 3 auto-detect) | `[x]` | Line removed |

**Context to chain forward after Batch 3:**
- Boot is on 3.x
- Persistence uses `jakarta.persistence.*`
- `@Type(type=...)` replaced by `AttributeConverter` pattern

---

### Batch 4 — claims-core-service (security + serialization)

**Files in scope:**
- `claims-core-service/src/main/java/com/acme/insurance/claims/config/JasyptConfig.java`
- `claims-core-service/src/main/java/com/acme/insurance/claims/legacy/XStreamClaimArchiver.java`
- `claims-core-service/src/main/java/com/acme/insurance/claims/web/ClaimController.java`
- Relevant pom entries (jasypt + xstream versions)

| # | Landmine | Status | Notes |
|---|----------|--------|-------|
| L11 | Jasypt: bump starter to 3.x (Boot 3 compatible); switch algorithm from `PBEWithMD5AndDES` to `PBEWITHHMACSHA512ANDAES_256`; re-encrypt the `ENC(...)` value in application.properties under the new algorithm | `[x]` | Starter 3.0.5; algorithm + IV generator updated; ENC() re-encrypted |
| L12 | XStream: bump to current; replace `AnyTypePermission.ANY` with explicit `allowTypes()`/`allowTypeHierarchy()`; add `--add-opens` JVM args where reflective access remains | `[x]` | Explicit allowTypes for Claim/ClaimStatus/Party; --add-opens in spring-boot-maven-plugin |

---

### Batch 5 — admin-web (JSF layer)

**Files in scope:**
- `admin-web/pom.xml` (JSF/Mojarra entries)
- `admin-web/src/main/java/com/acme/insurance/admin/bean/ClaimAdminBean.java`
- `admin-web/src/main/java/com/acme/insurance/admin/bean/PolicyAdminBean.java`
- `admin-web/src/main/java/com/acme/insurance/admin/converter/MoneyConverter.java`
- `admin-web/src/main/webapp/WEB-INF/web.xml` (JSF parts only)
- `admin-web/src/main/webapp/WEB-INF/faces-config.xml`
- `admin-web/src/main/webapp/admin/claims.xhtml` (JSF namespace fixes)

| # | Landmine | Status | Notes |
|---|----------|--------|-------|
| L13a | JSF managed beans: `@ManagedBean`/`@ViewScoped`/`@RequestScoped` (javax.faces.bean) → CDI `@Named` + `@jakarta.faces.view.ViewScoped` / `@jakarta.enterprise.context.RequestScoped` | `[x]` | ClaimAdminBean + PolicyAdminBean migrated to CDI |
| L13b | Converter: `javax.faces.convert.Converter` (raw) → `jakarta.faces.convert.Converter<T>` (generic); update method signatures | `[x]` | MoneyConverter now Converter<BigDecimal> with typed signatures |
| L13c | XML descriptors: update `xmlns` namespaces in web.xml (servlet 3.1→6.0), faces-config.xml (2.2→4.0); remove `<managed-bean>` blocks | `[x]` | Both updated; managed-bean block removed |
| L13d | XHTML taglib URIs: `http://java.sun.com/jsf/*` → `jakarta.faces.*` in claims.xhtml | `[x]` | h: and f: URIs updated |

---

### Batch 6 — admin-web (DWR rewrite)

**Files in scope:**
- `admin-web/src/main/java/com/acme/insurance/admin/dwr/ClaimLookupRemote.java`
- `admin-web/src/main/webapp/WEB-INF/dwr.xml`
- `admin-web/src/main/webapp/WEB-INF/web.xml` (DWR servlet mapping)
- `admin-web/src/main/webapp/admin/claims.xhtml` (DWR script tags + JS calls)

| # | Landmine | Status | Notes |
|---|----------|--------|-------|
| L13e | DWR → REST: **no jakarta release exists for DWR**; delete `dwr.xml` + DwrServlet mapping + engine.js/ClaimLookup.js script tags; expose `ClaimLookupRemote.describe()` as a JAX-RS/Spring MVC REST endpoint; replace inline `ClaimLookup.describe(...)` JS with `fetch()` | `[ ]` | This is a REWRITE, not an upgrade — flag it explicitly |

---

## 4. Decision Log

> Updated at the end of each session. If a session ends without updating this,
> the next session has no basis for consistent decisions.

| Date | Batch | Decision | Rationale |
|------|-------|----------|-----------|
| 2026-06-21 | Batch 1 | JAXB API: `jakarta.xml.bind:jakarta.xml.bind-api:4.0.2` | Standard Jakarta XML Binding 4.0 API |
| 2026-06-21 | Batch 1 | JAXB runtime: `org.glassfish.jaxb:jaxb-runtime:4.0.5` (runtime scope) | Reference implementation for Jakarta XML Binding 4.0 |
| 2026-06-21 | Batch 1 | `maven.compiler.source/target` → `17` in parent pom | Required for JDK 17 compilation |
| 2026-06-21 | Batch 2 | CXF `3.5.5` → `4.0.5` | CXF 4.x = jakarta namespace |
| 2026-06-21 | Batch 2 | Removed `javax.jws-api` + `jaxws-api` deps; CXF 4.x provides jakarta equivalents transitively | Cleaner dep tree |
| 2026-06-21 | Batch 2 | Added `jakarta.xml.bind-api` as explicit dep in policy-soap-service | Needed for JAXB annotations on request/response beans |
| 2026-06-21 | Batch 3 | Spring Boot `2.7.18` → `3.2.5` (BOM import in parent pom) | Umbrella change for jakarta namespace |
| 2026-06-21 | Batch 3 | Removed explicit `hibernate.version` pin + `hibernate-core` dep | Let Boot 3 manage Hibernate 6 |
| 2026-06-21 | Batch 3 | `@Type(type="yes_no")` → `YesNoConverter` (`AttributeConverter<Boolean,String>`) | Semantic rewrite required by Hibernate 6 |
| 2026-06-21 | Batch 3 | Dropped explicit `hibernate.dialect` from application.properties | Boot 3 / Hibernate 6 auto-detects |
| 2026-06-21 | Batch 4 | Jasypt starter `2.1.2` → `3.0.5`; algorithm `PBEWithMD5AndDES` → `PBEWITHHMACSHA512ANDAES_256` + `RandomIvGenerator` | Boot 3 compatible; stronger encryption |
| 2026-06-21 | Batch 4 | Re-encrypted `ENC()` value in application.properties under new algorithm | Old ciphertext invalid under new algo |
| 2026-06-21 | Batch 4 | XStream: `AnyTypePermission.ANY` → explicit `allowTypes()` + `allowTypeHierarchy()` | Security hardening for JDK 17 |
| 2026-06-21 | Batch 4 | Added `--add-opens` JVM args in spring-boot-maven-plugin | XStream reflective access on JDK 17 |
| 2026-06-21 | Batch 5 | Mojarra `2.3.9` → `4.0.7` (`org.glassfish:jakarta.faces`); Servlet API → `jakarta.servlet-api:6.0.0`; added CDI API `4.0.1` | Jakarta Faces 4 stack |
| 2026-06-21 | Batch 5 | `@ManagedBean`/`@ViewScoped`/`@RequestScoped` → CDI `@Named` + jakarta scopes | Faces 4 removes javax.faces.bean annotations |
| 2026-06-21 | Batch 5 | `Converter` (raw) → `Converter<BigDecimal>` (generic) with jakarta.faces package | Package + signature change |
| 2026-06-21 | Batch 5 | web.xml servlet 6.0, faces-config.xml 4.0, XHTML taglib URIs → jakarta.faces.* | XML namespace updates |
| 2026-06-21 | Batch 5 | Jetty `9.4.x` → `11.0.20` in parent pom | Jakarta servlet compatibility |

---

## 5. Known Traps (read before starting any session)

1. **L2 is silent at compile time.** A passing `mvn compile` does NOT mean JAXB works. Always verify `JAXBContext.newInstance(...)` resolves at runtime after Batch 1.

2. **L5 is a false negative.** `JaxWsServerFactoryBean` has the same name in CXF 3 and 4. Don't mark it "clean" — mark it "depends on L3+L4."

3. **L9 is not a find/replace.** `@Type(type="yes_no")` was removed in Hibernate 6, not relocated. Must become an `AttributeConverter<Boolean, String>`.

4. **L11 requires re-encryption.** Changing the Jasypt algorithm invalidates existing `ENC(...)` ciphertext in `application.properties`. Both the algorithm change and re-encrypt step must happen together.

5. **DWR has no migration path.** Do not attempt to upgrade DWR — there is no jakarta release. Any session that treats L13e as a version bump rather than a rewrite is wrong.

6. **The parent is a plain aggregator, not spring-boot-starter-parent.** The Boot version is controlled by a BOM import in `<dependencyManagement>`, not a `<parent>` version. Bumping Boot means updating the BOM import version, not a parent version tag.

7. **`maven.compiler.source/target` in the parent pom must be changed to `17`** as part of Batch 1 or before compilation checks on JDK 17 are meaningful.

---

## 6. Verification Commands

Run these after each batch on JDK 17:

```bash
# Compile the module and its upstream dependencies
mvn clean compile -pl <module> -am

# Check for any remaining javax EE imports in the module
grep -r "import javax\." <module>/src/main/java

# Full build after all batches are merged
mvn clean test
```

Smoke checks (from RUNNING.md) after full build:
- `shared-domain`: JAXB round-trip smoke test
- `policy-soap-service`: live CXF call
- `claims-core-service`: Boot app startup
- `admin-web`: JSF page render

---

## 7. Grading Reference

After all batches, score against `MIGRATION-ANSWER-KEY.md`:

| ID | Description | Result |
|----|-------------|--------|
| L1 | JAXB API package move | |
| L2 | JAXB runtime on classpath | |
| L3 | CXF 3→4 | |
| L4 | JAX-WS/JWS annotations | |
| L5 | CXF factory false-negative | |
| L6 | Request/response bean JAXB | |
| L7 | Spring Boot 2→3 | |
| L8 | javax.persistence→jakarta | |
| L9 | @Type(type=...) rewrite | |
| L10 | Hibernate dialect drop | |
| L11 | Jasypt re-encrypt | |
| L12 | XStream hardening | |
| L13 | JSF+DWR rewrite | |

MISSED and PARTIAL rows = skill-library backlog before applying this to production.
