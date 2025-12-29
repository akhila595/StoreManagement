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
            Path baseDir = Paths.get(uploadDir);
            Path usersDir = baseDir.resolve("users");
            Path productsDir = baseDir.resolve("products");

            Files.createDirectories(baseDir);
            Files.createDirectories(usersDir);
            Files.createDirectories(productsDir);

            System.out.println("✅ Upload directories ready:");
            System.out.println("   Base     : " + baseDir.toAbsolutePath());
            System.out.println("   Users    : " + usersDir.toAbsolutePath());
            System.out.println("   Products : " + productsDir.toAbsolutePath());

        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to create upload directories", e);
        }
    }
}
