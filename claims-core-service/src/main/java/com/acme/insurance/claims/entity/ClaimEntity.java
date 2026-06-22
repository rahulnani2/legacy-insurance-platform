package com.acme.insurance.claims.entity;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * LANDMINE [Hibernate]: javax.persistence.* + Hibernate-5-only @Type(type="yes_no").
 * Java 17 path: javax.persistence -> jakarta.persistence (Hibernate 6 / Boot 3) AND
 * @Type(type="...") string form is REMOVED in Hibernate 6 -> rewrite to @JdbcTypeCode
 * or a converter. GenerationType.AUTO default also changes (sequence vs identity).
 */
@Entity
@Table(name = "claim")
public class ClaimEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "claim_number", nullable = false, unique = true)
    private String claimNumber;

    @Column(name = "policy_number")
    private String policyNumber;

    @Column(name = "reserved_amount")
    private BigDecimal reservedAmount;

    @Column(name = "status")
    private String status;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "litigated")
    private boolean litigated;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public Long getId() { return id; }
    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    public BigDecimal getReservedAmount() { return reservedAmount; }
    public void setReservedAmount(BigDecimal reservedAmount) { this.reservedAmount = reservedAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isLitigated() { return litigated; }
    public void setLitigated(boolean litigated) { this.litigated = litigated; }
    public Instant getCreatedAt() { return createdAt; }
}
