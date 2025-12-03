package com.example.reporting.util;

import com.example.reporting.model.ImageData;
import org.apache.poi.sl.usermodel.PictureData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageUtils {

    public static ImageData createImageData(byte[] imageBytes, String contentType) {
        int pictureType = getPictureType(contentType);
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

    private static int getPictureType(String contentType) {
        if (contentType == null) {
            return PictureData.PictureType.PNG.ooxmlId;
        }

        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> PictureData.PictureType.JPEG.ooxmlId;
            case "image/png" -> PictureData.PictureType.PNG.ooxmlId;
            case "image/gif" -> PictureData.PictureType.GIF.ooxmlId;
            case "image/bmp" -> PictureData.PictureType.BMP.ooxmlId;
            case "image/tiff" -> PictureData.PictureType.TIFF.ooxmlId;
            default -> PictureData.PictureType.PNG.ooxmlId;
        };
    }
}