package com.example.demo.certificates;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CertificateCodeGeneratorTest {

    private final CertificateCodeGenerator generator = new CertificateCodeGenerator();

    @Test
    void generatesCodeInFourGroupsOfFourSeparatedByDashes() {
        String code = generator.generate();

        assertThat(code).matches("^[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}$");
    }

    @Test
    void generatesDistinctCodesAcrossManyCalls() {
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 500; i++) {
            codes.add(generator.generate());
        }

        // Extremely unlikely to collide at this sample size if the generator is truly random.
        assertThat(codes).hasSize(500);
    }

    @Test
    void neverUsesAmbiguousCharacters() {
        String code = generator.generate().replace("-", "");

        assertThat(code).doesNotContain("0", "O", "1", "I");
    }
}
