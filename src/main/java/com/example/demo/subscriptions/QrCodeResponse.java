package com.example.demo.subscriptions;

/**
 * The simulated payment charge: a payment code plus a QR code image
 * (base64-encoded PNG, embeddable directly as a data URI). Accessing this
 * code is NOT proof of payment by itself - it is confirmed explicitly via
 * {@code POST /subscriptions/{paymentCode}/confirm}, which is what actually
 * activates the subscription.
 */
public record QrCodeResponse(
        Long subscriptionId,
        String paymentCode,
        String qrCodeImageBase64,
        SubscriptionStatus status
) {
}
