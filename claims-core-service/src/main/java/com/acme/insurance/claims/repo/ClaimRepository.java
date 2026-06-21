package com.acme.insurance.claims.repo;

import com.acme.insurance.claims.entity.ClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClaimRepository extends JpaRepository<ClaimEntity, Long> {
    Optional<ClaimEntity> findByClaimNumber(String claimNumber);
}
