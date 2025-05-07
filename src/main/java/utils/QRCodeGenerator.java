package utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.scene.image.Image;
import models.Trajet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerator {

    private static final int QR_CODE_SIZE = 250;

    /**
     * Génère un code QR pour un trajet
     * @param trajet Le trajet pour lequel générer le code QR
     * @return Une image JavaFX contenant le code QR
     */
    public static Image generateQRCodeImage(Trajet trajet) throws WriterException, IOException {
        String qrContent = convertTrajetToString(trajet);
        return generateQRCode(qrContent);
    }

    /**
     * Génère un code QR à partir d'une chaîne de caractères
     * @param content Le contenu à encoder dans le code QR
     * @return Une image JavaFX contenant le code QR
     */
    public static Image generateQRCode(String content) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE, hints);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        return new Image(inputStream);
    }

    /**
     * Convertit un trajet en chaîne de caractères pour le code QR
     * @param trajet Le trajet à convertir
     * @return Une représentation textuelle du trajet
     */
    private static String convertTrajetToString(Trajet trajet) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(trajet.getId()).append("\n");
        sb.append("Date: ").append(trajet.getDate().format(dateFormatter)).append("\n");
        sb.append("Heure: ").append(trajet.getHeure()).append("h\n");
        sb.append("Destination: ").append(trajet.getDestination()).append("\n");
        sb.append("Transport: ").append(trajet.getTransport()).append("\n");
        sb.append("Durée: ").append(trajet.getDuree()).append(" heure(s)");

        return sb.toString();
    }
}

