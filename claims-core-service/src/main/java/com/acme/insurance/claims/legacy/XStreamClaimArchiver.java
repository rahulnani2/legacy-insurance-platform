package com.acme.insurance.claims.legacy;

import com.acme.insurance.shared.model.Claim;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * LANDMINE [XStream]: reflection-based (de)serialization of a shared Claim.
 * On Java 17 XStream's field reflection hits InaccessibleObjectException on JDK
 * internal types unless types are explicitly allowed and --add-opens is supplied.
 * The permissive setupSecurity() below is exactly the pattern that breaks.
 */
public class XStreamClaimArchiver {

    private final XStream xstream;

    public XStreamClaimArchiver() {
        this.xstream = new XStream(new StaxDriver());
        setupSecurity(this.xstream);
        this.xstream.alias("claim", Claim.class);
    }

    private void setupSecurity(XStream xs) {
        // Legacy permissive setup — flagged for tightening during migration.
        xs.addPermission(com.thoughtworks.xstream.security.AnyTypePermission.ANY);
    }

    public String archive(Claim claim) {
        return xstream.toXML(claim);
    }

    public Claim restore(String xml) {
        return (Claim) xstream.fromXML(xml);
    }
}
