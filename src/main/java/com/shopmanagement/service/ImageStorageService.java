package com.shopmanagement.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStorageService {

    @Value("${app.upload.user-images}")
    private String uploadDir;

    public String storeUserProfileImage(Long userId, MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new RuntimeException("Uploaded file is empty");
        }

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        File dir = new File(uploadDir);

        // ✅ CREATE DIRECTORY TREE IF MISSING
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Failed to create upload directory: " + uploadDir);
        }

        String original = file.getOriginalFilename();
        String ext = original.substring(original.lastIndexOf("."));

        String filename = "user-" + userId + "-" + UUID.randomUUID() + ext;

        File destination = new File(dir, filename);
        file.transferTo(destination);

        // Public URL (used by frontend)
        return "/images/users/" + filename;
    }
}
