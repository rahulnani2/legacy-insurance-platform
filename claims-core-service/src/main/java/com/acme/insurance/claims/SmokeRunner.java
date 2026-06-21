package com.acme.insurance.claims;

import com.acme.insurance.claims.entity.ClaimEntity;
import com.acme.insurance.claims.legacy.XStreamClaimArchiver;
import com.acme.insurance.claims.repo.ClaimRepository;
import com.acme.insurance.shared.model.Claim;
import com.acme.insurance.shared.model.ClaimStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Runtime smoke test that runs on application startup. Exercises:
 *  - L11 (Jasypt): @Value injection forces ENC() decryption at boot; if it prints a
 *    readable value, decrypt worked. (After migration, a forgotten re-encrypt fails HERE.)
 *  - L8/L9 (Hibernate/JPA): persist + read back a ClaimEntity.
 *  - L12 (XStream): round-trip a shared Claim that includes a java.time.LocalDate, which
 *    is exactly the field XStream reflects into — fine on Java 8, throws on 17 without --add-opens.
 * Run: mvn -pl claims-core-service spring-boot:run
 */
@Component
public class SmokeRunner implements CommandLineRunner {

    private final ClaimRepository repository;
    private final String partnerKey;

    public SmokeRunner(ClaimRepository repository,
                       @Value("${external.partner.api.key}") String partnerKey) {
        this.repository = repository;
        this.partnerKey = partnerKey;
    }

    @Override
    public void run(String... args) {
        System.out.println("==== claims-core smoke ====");
        System.out.println("[jasypt]  decrypted partner key = " + partnerKey);

        ClaimEntity e = new ClaimEntity();
        e.setClaimNumber("CLM-SMOKE-1");
        e.setPolicyNumber("POL-DEMO-1");
        e.setReservedAmount(new BigDecimal("5000.00"));
        e.setStatus("OPEN");
        e.setLitigated(false);
        repository.save(e);
        System.out.println("[jpa]     saved + found = "
                + repository.findByClaimNumber("CLM-SMOKE-1").isPresent());

        Claim c = new Claim();
        c.setClaimNumber("CLM-SMOKE-1");
        c.setStatus(ClaimStatus.OPEN);
        c.setReservedAmount(new BigDecimal("5000.00"));
        c.setLossDate(LocalDate.now()); // the field that trips XStream reflection on Java 17
        XStreamClaimArchiver archiver = new XStreamClaimArchiver();
        String xml = archiver.archive(c);
        Claim restored = archiver.restore(xml);
        System.out.println("[xstream] round-trip status = " + restored.getStatus());

        System.out.println("==== smoke OK ====");
    }
}
