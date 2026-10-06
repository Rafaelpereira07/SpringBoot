CREATE TABLE students (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(150)    NOT NULL,
    email         VARCHAR(180)    NOT NULL,
    password      VARCHAR(255)    NOT NULL,
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_students_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
