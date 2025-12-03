package com.example.reporting.util;

import com.example.reporting.model.ImageData;
import org.apache.poi.sl.usermodel.PictureData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageUtils {

    public static ImageData createImageData(byte[] imageBytes, String contentType) {
        PictureData.PictureType pictureType = getPictureType(contentType);
        return ImageData.builder()
            .imageBytes(imageBytes)
            .contentType(contentType)
            .pictureType(pictureType)
            .build();
    }

    public static ImageData loadImageFromFile(Path imagePath) throws IOException {
        byte[] imageBytes = Files.readAllBytes(imagePath);
        String contentType = Files.probeContentType(imagePath);
        return createImageData(imageBytes, contentType);
    }

    private static PictureData.PictureType getPictureType(String contentType) {
        if (contentType == null) {
            return PictureData.PictureType.PNG;
        }

        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> PictureData.PictureType.JPEG;
            case "image/png" -> PictureData.PictureType.PNG;
            case "image/gif" -> PictureData.PictureType.GIF;
            case "image/bmp" -> PictureData.PictureType.BMP;
            case "image/tiff" -> PictureData.PictureType.TIFF;
            default -> PictureData.PictureType.PNG;
        };
    }
}