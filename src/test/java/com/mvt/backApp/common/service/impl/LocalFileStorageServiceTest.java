package com.mvt.backApp.common.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class LocalFileStorageServiceTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void storesAndReadsFileInsideConfiguredDirectory() throws Exception {
        LocalFileStorageService service =
                new LocalFileStorageService(temporaryDirectory.toString());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "proof.pdf",
                "application/pdf",
                "test-content".getBytes()
        );

        String url = service.uploadFile(file, "generated-proof.pdf");

        assertThat(url)
                .isEqualTo("/uploads/documents/generated-proof.pdf");
        assertThat(service.downloadFile(url))
                .isEqualTo("test-content".getBytes());
    }
}
