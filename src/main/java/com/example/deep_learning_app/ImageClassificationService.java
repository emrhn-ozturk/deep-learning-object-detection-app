
package com.example.deep_learning_app;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class ImageClassificationService {

    private ZooModel<Image, DetectedObjects> model;

    public DetectedObjects detect(byte[] imageBytes)
            throws IOException, ModelException, TranslateException {

        Image image = ImageFactory.getInstance()
                .fromInputStream(new ByteArrayInputStream(imageBytes));

        try (Predictor<Image, DetectedObjects> predictor =
                     getModel().newPredictor()) {

            return predictor.predict(image);
        }
    }

    private synchronized ZooModel<Image, DetectedObjects> getModel()
            throws IOException, ModelException {

        if (model == null) {
            System.out.println("Nesne tespit modeli yükleniyor...");

            Criteria<Image, DetectedObjects> criteria =
                    Criteria.builder()
                            .optApplication(
                                    Application.CV.OBJECT_DETECTION)
                            .setTypes(
                                    Image.class,
                                    DetectedObjects.class)
                            .optArgument("threshold", 0.5)
                            .optEngine("PyTorch")
                            .build();

            model = criteria.loadModel();

            System.out.println("Nesne tespit modeli yüklendi!");
        }

        return model;
    }

    @PreDestroy
    public void close() {
        if (model != null) {
            model.close();
        }
    }
}
