package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService(tempDir.toString());
    }

    @Test
    void storeFile_validFile_returnsCorrectPath() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("receipt.pdf");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("content".getBytes()));

        String result = fileStorageService.storeFile(file, "receipts");

        assertThat(result).startsWith("receipts/");
        assertThat(result).endsWith("_receipt.pdf");
    }

    @Test
    void storeFile_fileNameWithPathTraversal_throwsException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("../secret.txt");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("x".getBytes()));

        assertThatThrownBy(() -> fileStorageService.storeFile(file, "receipts"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("invalid path sequence");
    }

    @Test
    void loadFileAsResource_validPath_returnsResolvedPath() throws IOException {
        Path subDir = tempDir.resolve("receipts");
        Files.createDirectories(subDir);
        Path file = subDir.resolve("test.pdf");
        Files.write(file, "content".getBytes());

        Path result = fileStorageService.loadFileAsResource("receipts/test.pdf");

        assertThat(result).isEqualTo(file.normalize().toAbsolutePath());
    }

    @Test
    void deleteFile_existingFile_fileIsRemoved() throws IOException {
        Path subDir = tempDir.resolve("receipts");
        Files.createDirectories(subDir);
        Path file = subDir.resolve("to-delete.pdf");
        Files.write(file, "content".getBytes());

        fileStorageService.deleteFile("receipts/to-delete.pdf");

        assertThat(Files.exists(file)).isFalse();
    }
}
