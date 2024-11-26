package ru.netology.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.dto.FileDto;
import ru.netology.model.File;
import ru.netology.repository.FileRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final Path storagePath;
    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository, @Value("${file.storage.location}") String storageLocation) {
        this.fileRepository = fileRepository;

        if (storageLocation == null || storageLocation.isBlank()) {
            throw new IllegalArgumentException("Storage location must not be null or empty.");
        }
        this.storagePath = Paths.get(storageLocation);
        initStorage();
    }

    private void initStorage() {
        try {
            Files.createDirectories(storagePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create storage directory at: " + storagePath, e);
        }
    }

    public List<FileDto> getFiles(int limit) {
        return fileRepository.findAll()
                .stream()
                .limit(limit)
                .map(file -> new FileDto(file.getFilename(), file.getFilesize()))
                .collect(Collectors.toList());
    }

    public void uploadFile(String filename, MultipartFile file) {
        try {
            Path destination = storagePath.resolve(filename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            File fileEntity = new File(filename, destination.toString(), file.getSize());
            fileRepository.save(fileEntity);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + filename, e);
        }
    }

    public Resource getFile(String filename) {
        try {
            Path filePath = storagePath.resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found or not readable: " + filename);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error reading file: " + filename, e);
        }
    }

    public void updateFile(String filename, Map<String, Object> fileData) {
        File fileEntity = fileRepository.findByFilename(filename)
                .orElseThrow(() -> new RuntimeException("File not found: " + filename));

        if (fileData.containsKey("newFilename")) {
            String newFilename = fileData.get("newFilename").toString();
            Path oldPath = storagePath.resolve(fileEntity.getFilename());
            Path newPath = storagePath.resolve(newFilename);

            try {
                Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
                fileEntity.setFilename(newFilename);
                fileEntity.setFilepath(newPath.toString());
            } catch (IOException e) {
                throw new RuntimeException("Error renaming file: " + filename, e);
            }
        }

        fileRepository.save(fileEntity);
    }

    public void deleteFile(String filename) {
        File fileEntity = fileRepository.findByFilename(filename)
                .orElseThrow(() -> new RuntimeException("File not found: " + filename));

        try {
            Path filePath = Paths.get(fileEntity.getFilepath());
            Files.deleteIfExists(filePath);
            fileRepository.delete(fileEntity);
        } catch (IOException e) {
            throw new RuntimeException("Error deleting file: " + filename, e);
        }
    }
}