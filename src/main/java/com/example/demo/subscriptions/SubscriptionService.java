package com.example.demo.subscriptions;

import com.example.demo.exceptions.BusinessRuleException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.plans.Plan;
import com.example.demo.plans.PlanService;
import com.example.demo.students.Student;
import com.example.demo.students.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

/**
 * Owns the simulated QR-Code payment flow end to end:
 * choose plan -> create PENDING subscription with a random, single-use
 * payment code -> render a QR code for that code -> explicit confirmation
 * activates the subscription (idempotently).
 */
@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final StudentRepository studentRepository;
    private final PlanService planService;
    private final QrCodeGenerator qrCodeGenerator;
    private final SecureRandom secureRandom = new SecureRandom();

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                                StudentRepository studentRepository,
                                PlanService planService,
                                QrCodeGenerator qrCodeGenerator) {
        this.subscriptionRepository = subscriptionRepository;
        this.studentRepository = studentRepository;
        this.planService = planService;
        this.qrCodeGenerator = qrCodeGenerator;
    }

    @Transactional
    public QrCodeResponse createPendingSubscription(Long studentId, SubscribeRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno nao encontrado."));
        Plan plan = planService.getActiveByType(request.planType());

        String paymentCode = generatePaymentCode();

        Subscription subscription = Subscription.builder()
                .student(student)
                .plan(plan)
                .status(SubscriptionStatus.PENDING)
                .paymentCode(paymentCode)
                .build();

        Subscription saved = subscriptionRepository.save(subscription);
        String qrImage = qrCodeGenerator.generateBase64Png(paymentCode);

        return new QrCodeResponse(saved.getId(), paymentCode, qrImage, saved.getStatus());
    }

    /**
     * Simulates the confirmation that would normally come from a payment
     * provider's webhook. Idempotent: confirming an already-active
     * subscription simply returns its current state rather than
     * re-activating or erroring.
     *
     * IMPORTANT: reaching this endpoint (e.g. by scanning/opening the QR
     * code) is treated purely as a simulated confirmation signal for this
     * project - it is not, and must never be presented as, proof of a real
     * Pix/payment transaction.
     */
    @Transactional
    public SubscriptionResponse confirmPayment(String paymentCode) {
        Subscription subscription = subscriptionRepository.findByPaymentCode(paymentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Codigo de pagamento invalido."));

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED
                || subscription.getStatus() == SubscriptionStatus.EXPIRED) {
            throw new BusinessRuleException("Esta cobranca nao pode mais ser confirmada.");
        }

        if (subscription.getStatus() == SubscriptionStatus.PENDING) {
            LocalDateTime now = LocalDateTime.now();
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setStartedAt(now);
            subscription.setActivatedAt(now);
            subscription.setExpiresAt(computeExpiration(subscription.getPlan(), now));
        }
        // If already ACTIVE, this is a no-op: idempotent activation.

        return toResponse(subscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> listForStudent(Long studentId) {
        return subscriptionRepository.findByStudentIdOrderByCreatedAtDesc(studentId).stream()
                .map(this::toResponse)
                .toList();
    }

    /** Used by LessonService to decide whether a video may be served. */
    @Transactional(readOnly = true)
    public boolean hasActiveSubscription(Long studentId) {
        return subscriptionRepository.findByStudentIdAndStatus(studentId, SubscriptionStatus.ACTIVE).stream()
                .anyMatch(Subscription::isCurrentlyValid);
    }

    private LocalDateTime computeExpiration(Plan plan, LocalDateTime from) {
        if (plan.getType().isLifetime()) {
            return null;
        }
        return from.plusDays(plan.getDurationDays());
    }

    private String generatePaymentCode() {
        byte[] bytes = new byte[24];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private SubscriptionResponse toResponse(Subscription subscription) {
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getPlan().getName(),
                subscription.getStatus(),
                subscription.getStartedAt(),
                subscription.getActivatedAt(),
                subscription.getExpiresAt()
        );
    }
}
