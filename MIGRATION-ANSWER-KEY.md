# MIGRATION ANSWER KEY — Java 8 → 17

> This is the ground truth. Do **not** feed it to your migration agent. Run your
> `CLAUDE.md` skill library against the repo blind, then diff its output against
> this file. Every landmine it silently leaves behind is a gap in your skill set,
> not in the code.

Scoring suggestion: for each landmine, mark **CAUGHT** (found + correctly fixed),
**PARTIAL** (found but fix is wrong/incomplete), or **MISSED**. The MISSED and
PARTIAL rows are exactly what to harden in your skills before you touch production.

---

## Landmine inventory (13 planted)

### shared-domain (migrate FIRST — everything depends on it)

**L1 — JAXB API removed from JDK**
- Files: `model/Policy.java`, `model/Claim.java`, `model/Party.java`, `model/ClaimStatus.java`, `util/LocalDateAdapter.java`, `xml/JaxbSupport.java`, plus `pom.xml`
- Why it breaks: `javax.xml.bind.*` shipped in the JDK through 8, deprecated in 9, removed in 11 (JEP 320). On 17 the imports don't resolve.
- Expected fix: imports `javax.xml.bind.*` → `jakarta.xml.bind.*` (incl. `...annotation.adapters.XmlAdapter`). In `pom.xml`, replace `javax.xml.bind:jaxb-api` with `jakarta.xml.bind:jakarta.xml.bind-api` (4.x) and `com.sun.xml.bind:jaxb-impl` → `org.glassfish.jaxb:jaxb-runtime` (4.x).
- Gotcha: this is the **context-chaining anchor**. Every downstream module imports these models; if your later sessions don't "remember" that the shared package moved to `jakarta`, they'll re-introduce `javax` or fail to compile against the new models.

**L2 — JAXBContext runtime resolution**
- File: `xml/JaxbSupport.java`
- Why it breaks: even with the API migrated, if no runtime impl is on the classpath, `JAXBContext.newInstance(...)` compiles fine but throws at runtime ("no implementation of JAXB-API found").
- Expected fix: ensure `jaxb-runtime` is present (runtime scope). This is a **silent** one — a compile-only check will pass it. Worth a runtime smoke test in your skill.

### policy-soap-service

**L3 — CXF major version**
- File: `pom.xml`
- Why it breaks: CXF 3.5.x is the last `javax.*` line; 4.x is `jakarta.*`.
- Expected fix: bump `cxf.version` to 4.x. Necessary but **not sufficient** on its own (see L4).

**L4 — JAX-WS / JWS annotations**
- Files: `ws/PolicyService.java`, `ws/PolicyServiceImpl.java`
- Why it breaks: `javax.jws.*` (WebService, WebMethod, WebParam, WebResult) and `javax.xml.ws.*` removed from JDK at 11 / replaced by jakarta.
- Expected fix: `javax.jws.*` → `jakarta.jws.*`; `javax.xml.ws.*` → `jakarta.xml.ws.*`. In `pom.xml` swap `javax.jws:javax.jws-api` and `javax.xml.ws:jaxws-api` for their jakarta equivalents (or let CXF 4 bring them transitively).

**L5 — CXF factory bean is a false negative**
- File: `config/PolicyEndpointPublisher.java`
- Why it's a trap: `org.apache.cxf.jaxws.JaxWsServerFactoryBean` has the **same class name** in CXF 3 and 4, so a diff/imports pass sees "no change needed." It silently links only against jakarta CXF 4 jars and against a SEI that must already be jakarta.
- Expected fix: no source edit, but the file is only correct once L3 + L4 are done. A good skill flags it as "depends on CXF bump" rather than "clean."

**L6 — request/response JAXB beans**
- Files: `ws/PolicyQuoteRequest.java`, `ws/PolicyQuoteResponse.java`
- Same as L1; same fix. Listed separately because they live in a different module — your batch for this service must re-apply the `javax→jakarta` JAXB move, not assume shared-domain covered it.

### claims-core-service

**L7 — Spring Boot 2 → 3**
- Files: `pom.xml` (BOM import in parent), `ClaimsApplication.java` and the whole module transitively.
- Why it breaks: Boot 3 requires Java 17 and moves the entire `javax.*` EE surface to `jakarta.*`.
- Expected fix: `spring.boot.version` → 3.x in the parent. This is the umbrella that forces L8–L11. Note the parent is a **plain aggregator**, so there's no `spring-boot-starter-parent` version to bump — it's the BOM import.

**L8 — Hibernate 5 → 6 / javax.persistence**
- File: `entity/ClaimEntity.java`
- Why it breaks: `javax.persistence.*` → `jakarta.persistence.*`; Hibernate 6 is jakarta-only.
- Expected fix: package move + bump `hibernate.version` to 6.x (or let Boot 3 manage it; remove the explicit pin).

**L9 — Hibernate `@Type(type="yes_no")` removed**
- File: `entity/ClaimEntity.java`
- Why it breaks: the string-form `@Type(type = "...")` was **removed** in Hibernate 6.
- Expected fix: replace with a JPA `AttributeConverter<Boolean,String>` (+ `@Convert`) or `@JdbcTypeCode`. **This is the highest-value catch** — package-rename tools fix L8 but leave L9 as a compile error, so it isolates whether your skill understands API semantics vs. just doing find/replace.

**L10 — Hibernate dialect resolution**
- File: `src/main/resources/application.properties`
- Why it breaks: explicit `org.hibernate.dialect.H2Dialect` + version-suffixed dialect behaviour changes in Hibernate 6 (auto-detection preferred; some dialect classes relocated/deprecated).
- Expected fix: drop the explicit dialect (let Boot 3 auto-detect) or update to the 6.x dialect form.

**L11 — Jasypt**
- Files: `pom.xml`, `ClaimsApplication.java`, `config/JasyptConfig.java`, `application.properties`
- Why it breaks: `jasypt-spring-boot-starter` 2.1.2 is Boot-2 only; jasypt core 1.9.x with `PBEWithMD5AndDES` is brittle on 17.
- Expected fix: bump starter to 3.x (Boot 3 compatible); move to `PBEWITHHMACSHA512ANDAES_256`; **re-encrypt** the `ENC(...)` value in `application.properties` under the new algorithm (the old ciphertext won't decrypt). Confirm the `@EnableEncryptableProperties` import/coordinates still resolve.

**L12 — XStream reflective access**
- File: `legacy/XStreamClaimArchiver.java`
- Why it breaks: reflection-based (de)serialization hits `InaccessibleObjectException` on JDK-internal types under 17's stronger encapsulation; `AnyTypePermission.ANY` is also a security smell.
- Expected fix: bump XStream to current; replace the permissive `AnyTypePermission` with explicit `allowTypes(...)`/`allowTypeHierarchy(...)`; add `--add-opens java.base/java.util=ALL-UNNAMED` (and similar) where reflective access remains, or migrate off XStream for these models.

### admin-web

**L13 — JSF Mojarra 2.x → Jakarta Faces 4 + DWR rewrite**
- Files: `bean/ClaimAdminBean.java`, `bean/PolicyAdminBean.java`, `converter/MoneyConverter.java`, `dwr/ClaimLookupRemote.java`, `webapp/WEB-INF/web.xml`, `webapp/WEB-INF/faces-config.xml`, `webapp/WEB-INF/dwr.xml`, `webapp/admin/claims.xhtml`, `pom.xml`
- Why it breaks (multi-part):
  - `javax.faces.bean.ManagedBean` / `ViewScoped` / `RequestScoped` **removed** in Faces 4 → CDI (`@jakarta.inject.Named` + `@jakarta.faces.view.ViewScoped` — note the package differs from `@jakarta.enterprise.context.RequestScoped`).
  - `javax.faces.convert.Converter` (raw) → `jakarta.faces.convert.Converter<T>` (generic) — package **and** signature change.
  - `web.xml` / `faces-config.xml` namespaces (`xmlns.jcp.org`) → jakarta namespaces; servlet 3.1 → 6.0; faces-config 2.2 → 4.0; drop XML `<managed-bean>` blocks.
  - `.xhtml` taglib URIs (`http://java.sun.com/jsf/*`) → `jakarta.faces.*`.
  - **DWR has no jakarta release** → this is a **REWRITE**, not a bump: delete `dwr.xml`, the `DwrServlet` mapping, and the `engine.js`/`ClaimLookup.js` script tags; expose `ClaimLookupRemote.describe(...)` as a REST endpoint and replace the inline `ClaimLookup.describe(...)` JS with a `fetch()` call.
- Gotcha: a migration tool will happily rename the JSF packages and leave DWR "compiling" — surfacing DWR as **rewrite-required** rather than **upgradeable** is the judgment test in this module.

---

## How to grade

| ID | Landmine | Module | CAUGHT / PARTIAL / MISSED |
|----|----------|--------|---------------------------|
| L1 | JAXB API package | shared-domain | |
| L2 | JAXB runtime impl | shared-domain | |
| L3 | CXF major bump | policy-soap | |
| L4 | JAX-WS/JWS annotations | policy-soap | |
| L5 | CXF factory false-negative | policy-soap | |
| L6 | request/response JAXB | policy-soap | |
| L7 | Spring Boot 2→3 | claims-core | |
| L8 | javax.persistence | claims-core | |
| L9 | @Type(type=...) removed | claims-core | |
| L10 | Hibernate dialect | claims-core | |
| L11 | Jasypt re-encrypt | claims-core | |
| L12 | XStream reflective access | claims-core | |
| L13 | JSF + DWR rewrite | admin-web | |

The rows you mark MISSED/PARTIAL are your skill-library backlog. The most common
silent misses on a find/replace-style pass are **L2, L5, L9, L11 (the re-encrypt
step), and the DWR-rewrite half of L13** — those are the ones worth a dedicated
companion skill.
