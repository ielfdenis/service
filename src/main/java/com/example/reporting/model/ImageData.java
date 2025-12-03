package com.example.reporting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.sl.usermodel.PictureData;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageData {
    private byte[] imageBytes;
    private String contentType;
    private PictureData.PictureType pictureType;
}