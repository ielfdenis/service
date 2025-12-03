package com.example.reporting.util;

import com.example.reporting.model.ImageData;
import org.apache.poi.sl.usermodel.PictureData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageUtilsTest {

    @Test
    void createImageData_shouldCreatePngByDefault() {
        byte[] imageBytes = new byte[]{1, 2, 3, 4};

        ImageData imageData = ImageUtils.createImageData(imageBytes, null);

        assertNotNull(imageData);
        assertArrayEquals(imageBytes, imageData.getImageBytes());
        assertEquals(PictureData.PictureType.PNG, imageData.getPictureType());
    }

    @Test
    void createImageData_shouldRecognizeJpeg() {
        byte[] imageBytes = new byte[]{1, 2, 3, 4};

        ImageData imageData = ImageUtils.createImageData(imageBytes, "image/jpeg");

        assertEquals(PictureData.PictureType.JPEG, imageData.getPictureType());
        assertEquals("image/jpeg", imageData.getContentType());
    }

    @Test
    void createImageData_shouldRecognizePng() {
        byte[] imageBytes = new byte[]{1, 2, 3, 4};

        ImageData imageData = ImageUtils.createImageData(imageBytes, "image/png");

        assertEquals(PictureData.PictureType.PNG, imageData.getPictureType());
        assertEquals("image/png", imageData.getContentType());
    }

    @Test
    void createImageData_shouldRecognizeGif() {
        byte[] imageBytes = new byte[]{1, 2, 3, 4};

        ImageData imageData = ImageUtils.createImageData(imageBytes, "image/gif");

        assertEquals(PictureData.PictureType.GIF, imageData.getPictureType());
    }

    @Test
    void createImageData_shouldRecognizeBmp() {
        byte[] imageBytes = new byte[]{1, 2, 3, 4};

        ImageData imageData = ImageUtils.createImageData(imageBytes, "image/bmp");

        assertEquals(PictureData.PictureType.BMP, imageData.getPictureType());
    }

    @Test
    void createImageData_shouldRecognizeTiff() {
        byte[] imageBytes = new byte[]{1, 2, 3, 4};

        ImageData imageData = ImageUtils.createImageData(imageBytes, "image/tiff");

        assertEquals(PictureData.PictureType.TIFF, imageData.getPictureType());
    }

    @Test
    void createImageData_shouldDefaultToPngForUnknownType() {
        byte[] imageBytes = new byte[]{1, 2, 3, 4};

        ImageData imageData = ImageUtils.createImageData(imageBytes, "image/unknown");

        assertEquals(PictureData.PictureType.PNG, imageData.getPictureType());
    }

    @Test
    void createImageData_shouldHandleEmptyBytes() {
        byte[] imageBytes = new byte[]{};

        ImageData imageData = ImageUtils.createImageData(imageBytes, "image/png");

        assertNotNull(imageData);
        assertEquals(0, imageData.getImageBytes().length);
    }
}