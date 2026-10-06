package com.example.demo.certificates;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Fully public: certificates are meant to be shareable and independently
 * verifiable (requirement 14) without requiring the viewer to log in.
 */
@RestController
@RequestMapping("/certificates")
@Tag(name = "Certificados")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping(value = "/{code}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> download(@PathVariable String code) {
        byte[] pdf = certificateService.getPdf(code);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"certificado-" + code + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/{code}/validate")
    public ResponseEntity<CertificateValidationResponse> validate(@PathVariable String code) {
        return ResponseEntity.ok(certificateService.validate(code));
    }
}
