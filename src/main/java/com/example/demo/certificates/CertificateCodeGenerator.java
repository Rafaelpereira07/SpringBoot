package com.example.demo.certificates;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Generates human-friendly, hard-to-guess certificate codes in groups of
 * four, e.g. "A8F3-92KD-71MX-4PQA" - matching the format shown on the
 * certificate itself.
 */
@Component
public class CertificateCodeGenerator {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // no 0/O/1/I to avoid confusion
    private static final int GROUPS = 4;
    private static final int GROUP_LENGTH = 4;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder sb = new StringBuilder();
        for (int g = 0; g < GROUPS; g++) {
            if (g > 0) {
                sb.append('-');
            }
            for (int i = 0; i < GROUP_LENGTH; i++) {
                sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
            }
        }
        return sb.toString();
    }
}
