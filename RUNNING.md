# RUNNING — Java 8 baseline (build AND execute)

Goal: confirm the whole reactor not only **compiles** on Java 8 but **runs** — so you
have a behavioural baseline, not just a green compile. The runtime checks matter
because the worst landmines (JAXB runtime impl, Jasypt decrypt, XStream reflection)
pass `compile` and only fail when executed.

This repo was authored offline and could not be built here (the sandbox can't reach
Maven Central), so the first `mvn` run on your machine also downloads dependencies.

---

## 1. Get a Java 8 toolchain

Easiest with SDKMAN:

```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 8.0.412-tem      # Temurin 8 (any 8.x is fine)
sdk install maven 3.9.9           # Maven 3.9.x runs fine on Java 8
sdk use java 8.0.412-tem
```

Verify Maven is actually running on 8 (this is the thing that bites people):

```bash
java -version          # -> 1.8.0_xxx
mvn -v                 # the "Java version:" line MUST say 1.8
```

If `mvn -v` shows a newer JDK, fix `JAVA_HOME`:

```bash
export JAVA_HOME="$(sdk home java 8.0.412-tem)"
```

(Windows: install Temurin 8, set `JAVA_HOME` to its folder, put `%JAVA_HOME%\bin`
first on `PATH`. macOS: `/usr/libexec/java_home -v 1.8`.)

---

## 2. Build the whole reactor (establishes the baseline + installs shared-domain)

From the repo root:

```bash
mvn -q clean install
```

`install` (not just `compile`) is required because the other three modules resolve
`shared-domain` from your local `~/.m2`. A clean pass here = your known-good baseline.
If anything fails to **compile**, fix the coordinate before migrating so accidental
breakage doesn't get mistaken for a planted landmine.

---

## 3. Execute each module

The four modules are different shapes, so "run" means something different for each.

### shared-domain — JAXB round-trip (library, no server)
```bash
mvn -pl shared-domain exec:java
```
Expect: marshalled `<policy>` XML printed, then `JAXB round-trip OK`.
This proves the runtime JAXB impl resolves (the silent L2 check).

### policy-soap-service — CXF SOAP endpoint + live client call
```bash
mvn -pl policy-soap-service exec:java
```
Expect: `endpoint published at http://localhost:9000/ws/policy`, then a `quoteId`,
`policyNumber`, `premium`, and `CXF SOAP round-trip OK`. The process exits itself.
(Port 9000 must be free.)

### claims-core-service — Spring Boot app (Jasypt + JPA + XStream all exercised on boot)
```bash
mvn -pl claims-core-service spring-boot:run
```
On startup the `SmokeRunner` prints:
```
[jasypt]  decrypted partner key = partner-api-secret-9F3K
[jpa]     saved + found = true
[xstream] round-trip status = OPEN
==== smoke OK ====
```
- `[jasypt]` printing a readable value = ENC() decryption worked (L11).
- Then the REST API is live on :8081; in another shell:
  ```bash
  curl -s localhost:8081/api/claims/CLM-SMOKE-1
  ```
- Ctrl-C to stop. (`mvn -pl claims-core-service package` also yields a runnable
  `target/claims-core-service-1.0.0-SNAPSHOT.jar` via the repackage goal.)

### admin-web — JSF + DWR (needs a servlet container)
```bash
mvn -pl admin-web jetty:run
```
Open <http://localhost:8080/admin-web/admin/claims.xhtml>. You should see the form;
the **Describe** button calls the DWR remote and pops an alert. Ctrl-C to stop.

This is the fiddliest module to run (JSF + DWR wiring), and the one I could not
execute here. For baseline purposes the bar is: the WAR builds, Jetty serves the
page, and the DWR call returns. If it renders and the button works on Java 8, you
have a behavioural baseline for the hardest migration target (Faces 4 + DWR rewrite).

---

## 4. Lock in the baseline, then prove it breaks on 17

Once all four run green on Java 8, switch toolchains and re-run the SAME commands:

```bash
sdk use java 17.0.11-tem
export JAVA_HOME="$(sdk home java 17.0.11-tem)"
mvn -q clean install            # expect compile failures: that's the target set
```

The diff between "runs on 8" and "fails on 17" is your migration worklist. Keep the
Java-8 smoke output saved — after you migrate, the same smoke commands on 17 are how
you detect runtime regressions (the ones compile won't show you).

---

## Quick reference

| Module | Command | Proves |
|--------|---------|--------|
| (all) | `mvn -q clean install` | compile baseline + installs shared-domain |
| shared-domain | `mvn -pl shared-domain exec:java` | JAXB API + runtime impl (L1/L2) |
| policy-soap-service | `mvn -pl policy-soap-service exec:java` | CXF/JAX-WS over the wire (L3/L4/L6) |
| claims-core-service | `mvn -pl claims-core-service spring-boot:run` | Jasypt/JPA/XStream (L8/L9/L11/L12) |
| admin-web | `mvn -pl admin-web jetty:run` | JSF render + DWR call (L13) |
