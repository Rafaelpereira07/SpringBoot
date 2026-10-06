package com.example.demo.subscriptions;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Renders a QR code image encoding only the opaque payment code - never a
 * URL, never student/plan data - so the simulated charge carries no
 * sensitive information (requirement: "Nao expor dados sensiveis no QR
 * Code").
 */
@Component
public class QrCodeGenerator {

    private static final int SIZE = 300;

    public String generateBase64Png(String content) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, SIZE, SIZE);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (WriterException | IOException e) {
            throw new IllegalStateException("Falha ao gerar QR Code.", e);
        }
    }
}
