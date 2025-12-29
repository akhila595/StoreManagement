package com.shopmanagement.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStorageService {

    @Value("${app.upload.image-dir}")
    private String uploadDir;

    public String storeUserProfileImage(Long userId, MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Uploaded file is empty");
        }

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        // users folder
        File dir = new File(uploadDir, "users");

        // create directory if missing
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Failed to create upload directory: " + dir.getAbsolutePath());
        }

        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf("."))
                : "";

        String filename = "user-" + userId + "-" + UUID.randomUUID() + ext;

        File destination = new File(dir, filename);
        file.transferTo(destination);

        // public URL
        return "/images/users/" + filename;
    }
}
