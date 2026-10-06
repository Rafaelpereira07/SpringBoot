CREATE TABLE certificates (
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    code                      VARCHAR(32)     NOT NULL,
    student_id                BIGINT          NOT NULL,
    course_id                 BIGINT          NOT NULL,
    course_duration_minutes   INT             NOT NULL,
    completed_at              DATETIME        NOT NULL,
    created_at                DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_certificates_code UNIQUE (code),
    CONSTRAINT uk_certificates_student_course UNIQUE (student_id, course_id),
    CONSTRAINT fk_certificates_student FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_certificates_course FOREIGN KEY (course_id) REFERENCES courses (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
