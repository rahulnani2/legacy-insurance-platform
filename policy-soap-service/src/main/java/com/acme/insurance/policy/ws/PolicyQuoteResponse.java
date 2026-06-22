package com.acme.insurance.policy.ws;

import com.acme.insurance.shared.model.Policy;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/** LANDMINE [JAXB]: SOAP response wrapping a shared-domain Policy. */
@XmlRootElement(name = "policyQuoteResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PolicyQuoteResponse {

    @XmlElement
    private String quoteId;
    @XmlElement
    private Policy policy;

    public String getQuoteId() { return quoteId; }
    public void setQuoteId(String quoteId) { this.quoteId = quoteId; }
    public Policy getPolicy() { return policy; }
    public void setPolicy(Policy policy) { this.policy = policy; }
}
