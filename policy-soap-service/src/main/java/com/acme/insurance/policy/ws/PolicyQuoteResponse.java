package com.acme.insurance.policy.ws;

import com.acme.insurance.shared.model.Policy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
