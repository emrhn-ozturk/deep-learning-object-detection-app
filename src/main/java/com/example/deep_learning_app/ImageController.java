package com.example.deep_learning_app;

import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.Rectangle;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class ImageController {

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png");

    private final ImageClassificationService detectionService;

    public ImageController(
            ImageClassificationService detectionService) {
        this.detectionService = detectionService;
    }

    @PostMapping(
            value = "/api/images/analyze",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, Object>> analyzeImage(
            @RequestParam("image") MultipartFile image) {

        if (image.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Lütfen bir görsel seç.")
            );
        }

        if (!ALLOWED_TYPES.contains(image.getContentType())) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Yalnızca JPG ve PNG kabul edilir.")
            );
        }

        try {
            DetectedObjects result =
                    detectionService.detect(image.getBytes());

            List<Map<String, Object>> detections =
                    new ArrayList<>();

            for (int i = 0;
                 i < result.getNumberOfObjects();
                 i++) {

                DetectedObjects.DetectedObject object =
                        result.item(i);

                Rectangle bounds =
                        object.getBoundingBox().getBounds();

                Map<String, Object> detection =
                        new LinkedHashMap<>();

                detection.put("label", object.getClassName());
                detection.put(
                        "confidence",
                        object.getProbability() * 100
                );

                // Nesnenin görsel içindeki konumu
                detection.put("x", bounds.getX());
                detection.put("y", bounds.getY());
                detection.put("width", bounds.getWidth());
                detection.put("height", bounds.getHeight());

                detections.add(detection);
            }

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Görsel analiz edildi!",
                            "fileName",
                            image.getOriginalFilename() == null
                                    ? "bilinmeyen"
                                    : image.getOriginalFilename(),
                            "detections", detections
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "message",
                            "Analiz başarısız. IntelliJ konsolunu kontrol et."
                    )
            );
        }
    }
}


