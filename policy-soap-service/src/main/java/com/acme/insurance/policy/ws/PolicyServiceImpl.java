package com.acme.insurance.policy.ws;

import com.acme.insurance.shared.model.Party;
import com.acme.insurance.shared.model.Policy;

import jakarta.jws.WebService;
import java.math.BigDecimal;
import java.util.UUID;

/** LANDMINE [CXF/JAX-WS]: @WebService(endpointInterface=...) impl. */
@WebService(endpointInterface = "com.acme.insurance.policy.ws.PolicyService",
            targetNamespace = "http://ws.policy.insurance.acme.com/")
public class PolicyServiceImpl implements PolicyService {

    @Override
    public PolicyQuoteResponse quote(PolicyQuoteRequest request) {
        Policy policy = new Policy();
        policy.setPolicyNumber("POL-" + UUID.randomUUID().toString().substring(0, 8));
        policy.setProductCode(request.getProductCode());
        policy.setEffectiveDate(request.getEffectiveDate());
        policy.setPremium(new BigDecimal("1249.95"));

        Party holder = new Party();
        holder.setPartyId("P-0001");
        holder.setFullName("Sample Holder");
        policy.setPolicyHolder(holder);

        PolicyQuoteResponse response = new PolicyQuoteResponse();
        response.setQuoteId(UUID.randomUUID().toString());
        response.setPolicy(policy);
        return response;
    }
}
