package com.mvt.backApp.common.service.impl;

import com.mvt.backApp.common.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@ConditionalOnProperty(
        name = "app.storage.provider",
        havingValue = "local",
        matchIfMissing = true
)
public class LocalFileStorageService
        implements FileStorageService {

    private final Path uploadLocation;

    public LocalFileStorageService(
            @Value("${app.storage.local.upload-dir}")
            String uploadDirectory
    ) {
        this.uploadLocation = Paths
                .get(uploadDirectory)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(uploadLocation);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not initialize the upload directory",
                    exception
            );
        }
    }

    @Override
    public String uploadFile(
            MultipartFile file,
            String fileName
    ) throws IOException {
        String safeFileName = Paths
                .get(fileName)
                .getFileName()
                .toString();

        Path target = uploadLocation
                .resolve(safeFileName)
                .normalize();

        ensureInsideUploadDirectory(target);

        Files.copy(
                file.getInputStream(),
                target,
                StandardCopyOption.REPLACE_EXISTING
        );

        return "/uploads/documents/" + safeFileName;
    }

    @Override
    public byte[] downloadFile(
            String fileUrl
    ) throws IOException {
        String fileName = fileUrl.substring(
                fileUrl.lastIndexOf('/') + 1
        );

        Path target = uploadLocation
                .resolve(fileName)
                .normalize();

        ensureInsideUploadDirectory(target);

        return Files.readAllBytes(target);
    }

    private void ensureInsideUploadDirectory(
            Path target
    ) {
        if (!target.startsWith(uploadLocation)) {
            throw new SecurityException(
                    "Invalid storage path"
            );
        }
    }
}