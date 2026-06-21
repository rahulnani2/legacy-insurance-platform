package com.acme.insurance.shared.model;

import com.acme.insurance.shared.util.LocalDateAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.time.LocalDate;

/** LANDMINE [JAXB]: @XmlRootElement + @XmlJavaTypeAdapter, all javax.xml.bind. */
@XmlRootElement(name = "policy")
@XmlAccessorType(XmlAccessType.FIELD)
public class Policy {

    @XmlElement(required = true)
    private String policyNumber;
    @XmlElement
    private String productCode;
    @XmlElement
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate effectiveDate;
    @XmlElement
    private BigDecimal premium;
    @XmlElement
    private Party policyHolder;

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
    public BigDecimal getPremium() { return premium; }
    public void setPremium(BigDecimal premium) { this.premium = premium; }
    public Party getPolicyHolder() { return policyHolder; }
    public void setPolicyHolder(Party policyHolder) { this.policyHolder = policyHolder; }
}
