package com.acme.insurance.claims;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * LANDMINE [Jasypt]: @EnableEncryptableProperties from jasypt-spring-boot 2.x.
 * The 2.x -> 3.x coordinates/packages shift when you move to Boot 3.
 */
@SpringBootApplication
@EnableEncryptableProperties
public class ClaimsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClaimsApplication.class, args);
    }
}
