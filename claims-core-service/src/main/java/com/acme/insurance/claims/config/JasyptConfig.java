package com.acme.insurance.claims.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LANDMINE [Jasypt]: org.jasypt 1.9.x StandardPBEStringEncryptor with the legacy
 * "PBEWithMD5AndDES" algorithm. On Java 17 the default JCE providers + jasypt 1.9.x
 * combination is brittle; the recommended path is jasypt-spring-boot 3.x and a
 * stronger algorithm (PBEWITHHMACSHA512ANDAES_256), which also changes the
 * decryptable ENC() values in application.properties.
 */
@Configuration
public class JasyptConfig {

    @Bean("jasyptStringEncryptor")
    public StandardPBEStringEncryptor stringEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(System.getenv().getOrDefault("JASYPT_PASSWORD", "dev-master-key"));
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }
}
