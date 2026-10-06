package com.example.demo.certificates;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.format.DateTimeFormatter;

/**
 * Renders the certificate content described in requirement 13 (student
 * name, course name, duration, completion date, validation code) as a PDF,
 * landscape A4, using only built-in PDF fonts so no external assets are
 * required.
 */
@Component
public class CertificatePdfGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generate(Certificate certificate) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
            document.addPage(page);

            PDFont titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDFont bodyFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDFont nameFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            float pageWidth = page.getMediaBox().getWidth();
            float centerY = page.getMediaBox().getHeight() / 2;

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                writeCentered(content, titleFont, 28, pageWidth, centerY + 160, "CERTIFICADO");

                writeCentered(content, bodyFont, 14, pageWidth, centerY + 100, "Certificamos que");

                writeCentered(content, nameFont, 22, pageWidth, centerY + 65,
                        certificate.getStudent().getName().toUpperCase());

                writeCentered(content, bodyFont, 14, pageWidth, centerY + 30, "concluiu o curso");

                writeCentered(content, nameFont, 18, pageWidth, centerY, certificate.getCourse().getTitle());

                String durationText = "Duracao: " + formatDuration(certificate.getCourseDurationMinutes());
                writeCentered(content, bodyFont, 12, pageWidth, centerY - 40, durationText);

                String dateText = "Data de conclusao: " + certificate.getCompletedAt().format(DATE_FORMAT);
                writeCentered(content, bodyFont, 12, pageWidth, centerY - 60, dateText);

                writeCentered(content, bodyFont, 12, pageWidth, centerY - 100, "Codigo de validacao:");
                writeCentered(content, titleFont, 14, pageWidth, centerY - 120, certificate.getCode());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao gerar PDF do certificado.", e);
        }
    }

    private void writeCentered(PDPageContentStream content, PDFont font, float fontSize,
                                float pageWidth, float y, String text) throws IOException {
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        float x = (pageWidth - textWidth) / 2;
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
    }

    private String formatDuration(Integer minutes) {
        if (minutes == null || minutes <= 0) {
            return "N/A";
        }
        int hours = minutes / 60;
        int remaining = minutes % 60;
        return hours + "h" + (remaining > 0 ? String.format("%02d", remaining) : "");
    }
}
