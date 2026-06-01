package org.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file, String subFolder) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        validateSafeFileName(originalFileName);
        String fileName = UUID.randomUUID().toString() + "_" + originalFileName;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(subFolder).normalize().toAbsolutePath();
            validatePathWithinBase(targetLocation, this.fileStorageLocation);
            Files.createDirectories(targetLocation);

            Path targetFile = targetLocation.resolve(fileName).normalize().toAbsolutePath();
            validatePathWithinBase(targetFile, targetLocation);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

            return subFolder + "/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Path loadFileAsResource(String filePath) {
        Path resolved = this.fileStorageLocation.resolve(filePath).normalize().toAbsolutePath();
        validatePathWithinBase(resolved, this.fileStorageLocation);
        return resolved;
    }

    public void deleteFile(String filePath) {
        try {
            Path targetLocation = this.fileStorageLocation.resolve(filePath).normalize().toAbsolutePath();
            validatePathWithinBase(targetLocation, this.fileStorageLocation);
            Files.deleteIfExists(targetLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file: " + filePath, ex);
        }
    }

    private void validateSafeFileName(String fileName) {
        if (!StringUtils.hasText(fileName) || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new RuntimeException("Sorry! Filename contains invalid path sequence: " + fileName);
        }
    }

    private void validatePathWithinBase(Path targetPath, Path basePath) {
        Path normalizedBase = basePath.normalize().toAbsolutePath();
        Path normalizedTarget = targetPath.normalize().toAbsolutePath();
        if (!normalizedTarget.startsWith(normalizedBase)) {
            throw new RuntimeException("Invalid file path: " + targetPath);
        }
    }
}
