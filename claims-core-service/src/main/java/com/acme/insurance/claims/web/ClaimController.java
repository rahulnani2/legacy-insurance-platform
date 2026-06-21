package com.acme.insurance.claims.web;

import com.acme.insurance.claims.entity.ClaimEntity;
import com.acme.insurance.claims.repo.ClaimRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimRepository repository;

    public ClaimController(ClaimRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ClaimEntity create(@RequestBody ClaimEntity claim) {
        return repository.save(claim);
    }

    @GetMapping("/{claimNumber}")
    public ResponseEntity<ClaimEntity> byNumber(@PathVariable String claimNumber) {
        return repository.findByClaimNumber(claimNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
