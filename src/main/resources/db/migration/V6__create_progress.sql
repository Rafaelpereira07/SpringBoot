CREATE TABLE progress (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id     BIGINT          NOT NULL,
    lesson_id      BIGINT          NOT NULL,
    completed      BOOLEAN         NOT NULL DEFAULT FALSE,
    completed_at   DATETIME        NULL,
    updated_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_progress_student_lesson UNIQUE (student_id, lesson_id),
    CONSTRAINT fk_progress_student FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_progress_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
