package com.acme.insurance.shared.model;

import com.acme.insurance.shared.util.LocalDateAdapter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.time.LocalDate;

/** LANDMINE [JAXB]: shared claim model marshalled by SOAP + persisted by claims-core. */
@XmlRootElement(name = "claim")
@XmlAccessorType(XmlAccessType.FIELD)
public class Claim {

    @XmlElement(required = true)
    private String claimNumber;
    @XmlElement
    private String policyNumber;
    @XmlElement
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate lossDate;
    @XmlElement
    private BigDecimal reservedAmount;
    @XmlElement
    private ClaimStatus status;
    @XmlElement
    private Party claimant;

    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    public LocalDate getLossDate() { return lossDate; }
    public void setLossDate(LocalDate lossDate) { this.lossDate = lossDate; }
    public BigDecimal getReservedAmount() { return reservedAmount; }
    public void setReservedAmount(BigDecimal reservedAmount) { this.reservedAmount = reservedAmount; }
    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }
    public Party getClaimant() { return claimant; }
    public void setClaimant(Party claimant) { this.claimant = claimant; }
}
