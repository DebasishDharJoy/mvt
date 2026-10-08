package com.mvt.backApp.common.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileStorageService {
    String uploadFile(MultipartFile file, String fileName) throws IOException;
    byte[] downloadFile(String fileUrl) throws IOException;
}
