package com.acme.insurance.policy.ws;

import com.acme.insurance.shared.util.LocalDateAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

/** LANDMINE [JAXB]: SOAP request bean, javax.xml.bind. */
@XmlRootElement(name = "policyQuoteRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class PolicyQuoteRequest {

    @XmlElement(required = true)
    private String productCode;
    @XmlElement
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate effectiveDate;
    @XmlElement
    private String postalCode;

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
}
