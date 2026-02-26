package com.shopmanagement.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStorageService {

    @Value("${app.upload.user-images}")
    private String uploadDir;

    // 🔥 Allowed extensions
    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("jpg", "jpeg", "png", "gif", "webp");

    // 🔥 Max file size (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    public String storeUserProfileImage(Long userId, MultipartFile file)
            throws IOException {

        if (file.isEmpty()) {
            throw new RuntimeException("Uploaded file is empty");
        }

        // 🔥 File size check
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds 5MB limit");
        }

        String original = file.getOriginalFilename();

        if (original == null || !original.contains(".")) {
            throw new RuntimeException("Invalid file name");
        }

        String ext = original.substring(original.lastIndexOf(".") + 1)
                .toLowerCase();

        // 🔥 Extension validation
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new RuntimeException("Only JPG, PNG, GIF, WEBP images allowed");
        }

        // 🔥 Create directory safely
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        String filename = "user-" + userId + "-" +
                UUID.randomUUID() + "." + ext;

        Path targetLocation = uploadPath.resolve(filename);

        file.transferTo(targetLocation.toFile());

        return "/images/users/" + filename;
    }
}