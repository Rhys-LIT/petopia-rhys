package com.example.assignmenttwo_starter.utilities;

import net.glxn.qrgen.javase.QRCode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QrCodeUtility {
    public static BufferedImage getQrCodeImage(String text, int pixelLength) throws IOException {
        ByteArrayOutputStream stream = QRCode
                .from(text)
                .withSize(pixelLength, pixelLength)
                .stream();

        return ImageIO.read(new ByteArrayInputStream(stream.toByteArray()));
    }


    private QrCodeUtility() {
        throw new IllegalStateException("Utility class");
    }
}
