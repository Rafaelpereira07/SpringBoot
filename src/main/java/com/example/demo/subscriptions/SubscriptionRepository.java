package com.example.demo.subscriptions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByPaymentCode(String paymentCode);
    List<Subscription> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    List<Subscription> findByStudentIdAndStatus(Long studentId, SubscriptionStatus status);
}
