CREATE TABLE plans (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(50)     NOT NULL,
    type           VARCHAR(20)     NOT NULL,
    description    VARCHAR(255),
    duration_days  INT,
    price          DECIMAL(10,2)   NOT NULL,
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at     DATETIME        NULL,
    CONSTRAINT uk_plans_type UNIQUE (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
