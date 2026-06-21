package com.acme.insurance.policy.config;

import com.acme.insurance.policy.ws.PolicyService;
import com.acme.insurance.policy.ws.PolicyServiceImpl;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;

/**
 * LANDMINE [CXF]: org.apache.cxf.jaxws.JaxWsServerFactoryBean.
 * API class names are stable 3.x -> 4.x, so this LOOKS untouched, but it only links
 * against the jakarta-based CXF 4 jars. A naive "imports look fine" pass will miss
 * that the dependency bump is mandatory and the SEI it wires must already be jakarta.
 */
public class PolicyEndpointPublisher {

    public void publish() {
        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
        factory.setServiceClass(PolicyService.class);
        factory.setAddress("http://localhost:9000/ws/policy");
        factory.setServiceBean(new PolicyServiceImpl());
        factory.create();
    }
}
