package com.shopmanagement;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ImageUploadDirectoryInitializer {

    @Value("${app.upload.image-dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("✅ Created upload directory: " + path.toAbsolutePath());
            } else {
                System.out.println("✅ Upload directory already exists: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to create upload directory", e);
        }
    }
    //
}

