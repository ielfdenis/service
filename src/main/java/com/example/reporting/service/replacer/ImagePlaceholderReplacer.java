package com.example.reporting.service.replacer;

import com.example.reporting.model.ImageData;
import com.example.reporting.model.Placeholder;
import com.example.reporting.model.PlaceholderType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.springframework.stereotype.Component;

import java.awt.geom.Rectangle2D;

@Slf4j
@Component
public class ImagePlaceholderReplacer implements PlaceholderReplacer {

    @Override
    public boolean canHandle(Placeholder placeholder) {
        return placeholder.getType() == PlaceholderType.IMAGE;
    }

    @Override
    public void replace(XMLSlideShow presentation, Placeholder placeholder) throws Exception {
        String searchPattern = "{{" + placeholder.getKey() + "}}";
        ImageData imageData = (ImageData) placeholder.getValue();

        for (XSLFSlide slide : presentation.getSlides()) {
            for (XSLFShape shape : slide.getShapes()) {
                if (shape instanceof XSLFTextShape) {
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    String text = textShape.getText();

                    if (text != null && text.contains(searchPattern)) {
                        Rectangle2D anchor = shape.getAnchor();

                        PictureData pictureData = presentation.addPicture(
                            imageData.getImageBytes(),
                            imageData.getPictureType()
                        );

                        XSLFPictureShape picture = slide.createPicture(pictureData);
                        picture.setAnchor(anchor);

                        slide.removeShape(shape);

                        log.info("Replaced image placeholder: {} with image", placeholder.getKey());
                        break;
                    }
                }
            }
        }
    }
}