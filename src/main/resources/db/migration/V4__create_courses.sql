CREATE TABLE courses (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    title             VARCHAR(150)    NOT NULL,
    slug              VARCHAR(170)    NOT NULL,
    description       TEXT,
    language          VARCHAR(60),
    level             VARCHAR(20)     NOT NULL,
    thumbnail_url     VARCHAR(500),
    duration_minutes  INT             NOT NULL DEFAULT 0,
    created_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at        DATETIME        NULL,
    CONSTRAINT uk_courses_slug UNIQUE (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
