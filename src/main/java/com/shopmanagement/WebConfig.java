package com.shopmanagement;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Physical upload directory
     * Example: uploads/images
     */
    @Value("${app.upload.image-dir}")
    private String uploadDir;

    // ===============================
    // CORS CONFIGURATION
    // ===============================
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders(
                        "Authorization",
                        "Content-Type",
                        "X-Is-SuperAdmin",
                        "X-Customer-Id"
                )
                .exposedHeaders("Authorization")
                .allowCredentials(true);
    }

    // ===============================
    // STATIC IMAGE SERVING
    // ===============================
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        registry
            .addResourceHandler("/images/**")
            .addResourceLocations("file:" + uploadPath + "/");
    }
}
