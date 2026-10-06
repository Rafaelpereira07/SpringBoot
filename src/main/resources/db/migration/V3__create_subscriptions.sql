CREATE TABLE subscriptions (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id     BIGINT          NOT NULL,
    plan_id        BIGINT          NOT NULL,
    status         VARCHAR(20)     NOT NULL,
    payment_code   VARCHAR(64)     NOT NULL,
    started_at     DATETIME        NULL,
    activated_at   DATETIME        NULL,
    expires_at     DATETIME        NULL,
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_subscriptions_payment_code UNIQUE (payment_code),
    CONSTRAINT fk_subscriptions_student FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_subscriptions_plan FOREIGN KEY (plan_id) REFERENCES plans (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_subscriptions_student ON subscriptions (student_id);
CREATE INDEX idx_subscriptions_status ON subscriptions (status);
