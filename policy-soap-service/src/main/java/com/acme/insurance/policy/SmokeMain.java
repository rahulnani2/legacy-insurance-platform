package com.acme.insurance.policy;

import com.acme.insurance.policy.config.PolicyEndpointPublisher;
import com.acme.insurance.policy.ws.PolicyQuoteRequest;
import com.acme.insurance.policy.ws.PolicyQuoteResponse;
import com.acme.insurance.policy.ws.PolicyService;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import java.time.LocalDate;

/**
 * Runtime smoke test. Exercises L3/L4/L6: publishes the CXF JAX-WS endpoint on an
 * embedded Jetty transport, then calls it with a generated SOAP client over the wire.
 * On Java 8 + CXF 3.5 this passes. Run: mvn -pl policy-soap-service exec:java
 */
public final class SmokeMain {

    public static void main(String[] args) {
        new PolicyEndpointPublisher().publish();
        System.out.println("endpoint published at http://localhost:9000/ws/policy");

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(PolicyService.class);
        factory.setAddress("http://localhost:9000/ws/policy");
        PolicyService client = (PolicyService) factory.create();

        PolicyQuoteRequest req = new PolicyQuoteRequest();
        req.setProductCode("AUTO-STD");
        req.setEffectiveDate(LocalDate.now());
        req.setPostalCode("M5V");

        PolicyQuoteResponse resp = client.quote(req);
        System.out.println("quoteId      = " + resp.getQuoteId());
        System.out.println("policyNumber = " + resp.getPolicy().getPolicyNumber());
        System.out.println("premium      = " + resp.getPolicy().getPremium());
        System.out.println("CXF SOAP round-trip OK");

        System.exit(0); // embedded server thread otherwise keeps the JVM alive
    }
}
