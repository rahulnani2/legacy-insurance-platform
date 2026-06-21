# Legacy Insurance Platform — a Java 8 → 17 Migration Gym

A deliberately-seeded multi-module Maven monorepo for **rehearsing your Claude Code
migration workflow before you point it at real production services.** Every
incompatibility here was planted on purpose, drawn from the stack you flagged in your
real POC, so you can grade your migration pass against a known answer key instead of
guessing whether it missed something.

It's an insurance domain (policy/claims) because that naturally motivates the legacy
tech — SOAP web services, XML binding, a JSF admin UI, encrypted config.

## The four modules

```
legacy-insurance-platform/
├── shared-domain          JAXB models (javax.xml.bind) — the dependency root
├── policy-soap-service    Apache CXF 3.5 + JAX-WS (javax.jws / javax.xml.ws)
├── claims-core-service    Spring Boot 2.7 + Hibernate 5 + Jasypt 1.9 + XStream
└── admin-web              Mojarra JSF 2.3 (javax.faces) + DWR 3 (no jakarta path)
```

`shared-domain` is depended on by the other three — that's what makes this a
*cross-session* exercise rather than three isolated migrations. Migrate it first,
then carry its decisions forward through `CLAUDE.md` to prove your context-chaining
holds across stateless sessions.

## Landmines planted (full detail in `MIGRATION-ANSWER-KEY.md`)

- **JAXB** `javax.xml.bind.*` → removed in JDK 11; needs `jakarta.xml.bind` + runtime impl
- **CXF / JAX-WS** 3.x `javax.jws`/`javax.xml.ws` → 4.x jakarta (with a false-negative trap)
- **Spring Boot** 2.7 → 3.x umbrella
- **Hibernate** 5 `javax.persistence` → 6 `jakarta.persistence`, incl. removed `@Type(type=...)`
- **Jasypt** 1.9.x / Boot-2 starter → 3.x + re-encrypt under a stronger algorithm
- **XStream** reflective access → `InaccessibleObjectException` on 17
- **JSF** Mojarra 2.3 `javax.faces` → Jakarta Faces 4 + CDI managed beans
- **DWR** → no jakarta release: **rewrite as REST**, don't bump

## How to use it

1. **Baseline (JDK 8).** `mvn -q clean install` should pass, and each module has a
   runnable smoke check (JAXB round-trip, live CXF call, Boot app, JSF page). Full
   step-by-step setup + per-module run commands are in **`RUNNING.md`**. *(Authored
   offline; not compiled in-sandbox — run the baseline yourself first and fix any
   coordinate hiccups so they don't look like planted bugs.)*
2. **Confirm it fails on JDK 17.** Switch toolchains; the failure set is your target.
3. **Run your migration workflow blind.** Point your `CLAUDE.md` skill library at the
   repo. Follow `BATCHING-PLAN.md` to rehearse 15–20-file batches across sessions —
   or better, let your own skills decide the batching and compare.
4. **Grade.** Diff the result against `MIGRATION-ANSWER-KEY.md`. Mark each of the 13
   landmines CAUGHT / PARTIAL / MISSED. The MISSED rows are your skill backlog.
5. **Reset and re-run** after hardening a skill. Because the landmines are fixed and
   known, you get a repeatable benchmark — the thing a found repo can never give you.

## What this is and isn't

It **is** a controlled, gradable rehearsal harness covering the exact API-break
surface of a real Java 8→17 / javax→jakarta migration. It **isn't** production-scale:
it's a few files per module, sized so the *batching seams* are the interesting part,
not raw line count. Use it to harden the workflow; the real services bring the scale.

See `MIGRATION-ANSWER-KEY.md` for the ground truth and `BATCHING-PLAN.md` for the
session-by-session decomposition.
