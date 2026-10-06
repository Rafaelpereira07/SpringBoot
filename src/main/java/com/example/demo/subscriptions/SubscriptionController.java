package com.example.demo.subscriptions;

import com.example.demo.security.CurrentStudentProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@Tag(name = "Assinaturas")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final CurrentStudentProvider currentStudentProvider;

    public SubscriptionController(SubscriptionService subscriptionService, CurrentStudentProvider currentStudentProvider) {
        this.subscriptionService = subscriptionService;
        this.currentStudentProvider = currentStudentProvider;
    }

    /** Protected: an authenticated student chooses a plan and receives a simulated QR-code charge. */
    @PostMapping
    public ResponseEntity<QrCodeResponse> subscribe(@Valid @RequestBody SubscribeRequest request) {
        Long studentId = currentStudentProvider.requireStudentId();
        QrCodeResponse response = subscriptionService.createPendingSubscription(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Simulates the payment confirmation callback. Public by design (this is
     * what "reading" the QR code triggers), but it only ever activates the
     * one PENDING subscription matching this exact, unguessable code -
     * accessing an arbitrary URL never counts as proof of payment for any
     * other subscription.
     */
    @PostMapping("/{paymentCode}/confirm")
    public ResponseEntity<SubscriptionResponse> confirm(@PathVariable String paymentCode) {
        return ResponseEntity.ok(subscriptionService.confirmPayment(paymentCode));
    }

    @GetMapping("/me")
    public ResponseEntity<List<SubscriptionResponse>> mine() {
        Long studentId = currentStudentProvider.requireStudentId();
        return ResponseEntity.ok(subscriptionService.listForStudent(studentId));
    }
}
