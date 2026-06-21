# BATCHING PLAN — Rehearsing Cross-Session Context-Chaining

The point of this monorepo (vs. a single service) is to rehearse the operational
shift you flagged for the real POC: **manual package-based batching, ~15–20 files
per session, with `CLAUDE.md` carrying state forward across stateless sessions.**

There are ~30 files here, so the whole thing won't fit one context window cleanly —
which is the point. Below is a suggested decomposition. Run it, then compare against
how your own skills choose to batch.

## File counts per module

| Module | Source files | Notes |
|--------|--------------|-------|
| shared-domain | 6 + pom | The dependency root. Migrate first. |
| policy-soap-service | 5 + pom | Depends on shared-domain models. |
| claims-core-service | 6 + pom + properties | Depends on shared-domain; biggest API surface. |
| admin-web | 4 java + 4 webapp + pom | Spans two concerns (JSF + DWR); split it. |

## Suggested batch sequence (dependency-ordered)

**Batch 1 — shared-domain (the anchor)**
All 6 source files + pom. ~7 files. Migrate JAXB `javax → jakarta` here.
→ **Context to chain forward** (write into `CLAUDE.md` before the next session):
  - "shared-domain models now use `jakarta.xml.bind.*`."
  - "`jaxb-runtime` (org.glassfish.jaxb) is the chosen runtime impl."
  - "`LocalDateAdapter` extends `jakarta.xml.bind.annotation.adapters.XmlAdapter`."
  This is the test: a stateless session 2 has *no memory* of session 1 unless you
  encode these decisions. If batch 2 re-introduces `javax`, your chaining failed.

**Batch 2 — policy-soap-service**
5 source files + pom. ~6 files. Apply: CXF 3→4, JAX-WS/JWS jakarta move, and the
*same* JAXB move on the request/response beans (using the decisions from Batch 1).
→ Chain forward: "CXF is on 4.x; `JaxWsServerFactoryBean` import unchanged but
  jakarta-linked."

**Batch 3 — claims-core-service (entities + persistence)**
`pom.xml`, `ClaimsApplication.java`, `entity/ClaimEntity.java`, `repo/ClaimRepository.java`,
`application.properties`. ~5 files. Apply: Boot 2→3, Hibernate 5→6, the `@Type`
rewrite (L9), dialect (L10).
→ Chain forward: "Boot is 3.x; persistence is `jakarta.persistence`; `@Type(type=...)`
  replaced by an AttributeConverter."

**Batch 4 — claims-core-service (security + legacy serialization)**
`config/JasyptConfig.java`, `legacy/XStreamClaimArchiver.java`, `web/ClaimController.java`,
+ the jasypt/xstream pom entries. ~4 files. Apply Jasypt 3.x + re-encrypt (L11) and
XStream hardening (L12). Splitting claims-core across Batch 3/4 rehearses splitting a
*single* large service across sessions — your real 15–20-file constraint.

**Batch 5 — admin-web (JSF)**
`bean/*`, `converter/MoneyConverter.java`, `faces-config.xml`, `web.xml` (JSF parts),
`claims.xhtml` namespaces, pom JSF entry. ~7 files. Apply Faces 4 + CDI rewrite.

**Batch 6 — admin-web (DWR rewrite)**
`dwr/ClaimLookupRemote.java`, `dwr.xml`, the DWR parts of `web.xml`, the DWR JS in
`claims.xhtml`. ~4 files. This one is deliberately a *rewrite* batch, not an upgrade —
good for rehearsing how your skills handle "no migration path exists."

## What to actually measure

1. **Did context survive the session boundary?** The single most important signal:
   does Batch 2+ honour the `jakarta` + runtime-impl decisions from Batch 1 without
   you re-stating them, purely from what you wrote into `CLAUDE.md`?
2. **Did batching hide a cross-cutting landmine?** L5 (CXF false-negative) and L9
   (`@Type`) are easy to lose at a batch seam. Check the answer key.
3. **Batch sizing:** did any batch blow the context window? If so, your real-POC
   package boundaries may need to be finer than you think.

## Baseline first

Before any migration, establish the green baseline on JDK 8:

```
mvn -q -DskipTests clean compile     # must pass on JDK 8
```

Then switch your toolchain to JDK 17 and confirm it now FAILS — that failure set is
your starting point. (Note: this repo was authored offline and has **not** been
compiled here, because this sandbox can't reach Maven Central. Run the baseline build
in your own environment first; if a dependency coordinate needs a tweak, fix it before
starting so accidental breakage doesn't masquerade as a planted landmine.)
