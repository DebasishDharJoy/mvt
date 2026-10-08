package com.mvt.backApp.common.service.impl;

import com.mvt.backApp.common.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageService implements FileStorageService {

    private final Path uploadLocation;

    public LocalFileStorageService(@Value("${app.storage.local.upload-dir}") String uploadDir) {
        this.uploadLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload directory.", ex);
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String fileName) throws IOException {
        Path targetLocation = this.uploadLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation);
        // Return relative path or URL for DB storage
        return "/uploads/documents/" + fileName;
    }

    @Override
    public byte[] downloadFile(String fileUrl) throws IOException {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        Path filePath = this.uploadLocation.resolve(fileName).normalize();
        return Files.readAllBytes(filePath);
    }
}
