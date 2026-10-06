package com.example.demo.certificates;

import com.example.demo.courses.Course;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.students.Student;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final CertificateCodeGenerator codeGenerator;
    private final CertificatePdfGenerator pdfGenerator;

    public CertificateService(CertificateRepository certificateRepository,
                               CertificateCodeGenerator codeGenerator,
                               CertificatePdfGenerator pdfGenerator) {
        this.certificateRepository = certificateRepository;
        this.codeGenerator = codeGenerator;
        this.pdfGenerator = pdfGenerator;
    }

    /**
     * Called by ProgressService once every active lesson of a course has
     * been completed. Idempotent: if a certificate for this
     * student+course already exists (unique constraint on
     * student_id+course_id), it is returned as-is instead of creating a
     * duplicate - a student can only ever complete a given course once.
     */
    @Transactional
    public Certificate issueIfNotExists(Student student, Course course, int courseDurationMinutes) {
        return certificateRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                .orElseGet(() -> {
                    Certificate certificate = Certificate.builder()
                            .code(generateUniqueCode())
                            .student(student)
                            .course(course)
                            .courseDurationMinutes(courseDurationMinutes)
                            .completedAt(LocalDateTime.now())
                            .build();
                    return certificateRepository.save(certificate);
                });
    }

    @Transactional(readOnly = true)
    public byte[] getPdf(String code) {
        Certificate certificate = getByCode(code);
        return pdfGenerator.generate(certificate);
    }

    @Transactional(readOnly = true)
    public CertificateValidationResponse validate(String code) {
        return certificateRepository.findByCode(code)
                .map(certificate -> new CertificateValidationResponse(
                        true,
                        certificate.getStudent().getName(),
                        certificate.getCourse().getTitle(),
                        certificate.getCompletedAt().toLocalDate(),
                        certificate.getCourseDurationMinutes()
                ))
                .orElseGet(CertificateValidationResponse::invalid);
    }

    private Certificate getByCode(String code) {
        return certificateRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado nao encontrado."));
    }

    private String generateUniqueCode() {
        String candidate;
        do {
            candidate = codeGenerator.generate();
        } while (certificateRepository.findByCode(candidate).isPresent());
        return candidate;
    }
}
