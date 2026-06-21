package com.acme.insurance.shared;

import com.acme.insurance.shared.model.Party;
import com.acme.insurance.shared.model.Policy;
import com.acme.insurance.shared.xml.JaxbSupport;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Runtime smoke test. Exercises L1/L2: a JAXB marshal+unmarshal round-trip.
 * On Java 8 this passes. After migration, if the jakarta API is present but no
 * runtime impl is on the classpath, JaxbSupport throws here (not at compile time).
 * Run: mvn -pl shared-domain exec:java
 */
public final class SmokeMain {

    public static void main(String[] args) {
        Policy p = new Policy();
        p.setPolicyNumber("POL-DEMO-1");
        p.setProductCode("AUTO-STD");
        p.setEffectiveDate(LocalDate.now());
        p.setPremium(new BigDecimal("1249.95"));

        Party holder = new Party();
        holder.setPartyId("P-1");
        holder.setFullName("Ada Lovelace");
        p.setPolicyHolder(holder);

        String xml = JaxbSupport.toXml(p);
        System.out.println("---- marshalled policy ----");
        System.out.println(xml);

        Policy back = JaxbSupport.fromXml(xml, Policy.class);
        System.out.println("round-trip policyNumber  = " + back.getPolicyNumber());
        System.out.println("round-trip effectiveDate = " + back.getEffectiveDate());
        System.out.println("JAXB round-trip OK");
    }
}
