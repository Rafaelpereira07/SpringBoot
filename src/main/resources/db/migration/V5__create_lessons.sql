CREATE TABLE lessons (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id          BIGINT          NOT NULL,
    title              VARCHAR(150)    NOT NULL,
    slug               VARCHAR(170)    NOT NULL,
    description        TEXT,
    thumbnail_url      VARCHAR(500),
    video_url          VARCHAR(500)    NOT NULL,
    duration_seconds   INT             NOT NULL DEFAULT 0,
    lesson_order       INT             NOT NULL DEFAULT 0,
    created_at         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at         DATETIME        NULL,
    CONSTRAINT uk_lessons_course_slug UNIQUE (course_id, slug),
    CONSTRAINT fk_lessons_course FOREIGN KEY (course_id) REFERENCES courses (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_lessons_course_order ON lessons (course_id, lesson_order);
