package ru.netology.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.service.FileService;
import ru.netology.dto.FileDto;
import ru.netology.model.Error;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService; // Сервис для работы с файлами

    // Получение списка файлов
    @GetMapping("/list")
    public ResponseEntity<List<FileDto>> getFiles(@RequestParam("limit") int limit) {
        List<FileDto> files = fileService.getFiles(limit);
        return ResponseEntity.ok(files);
    }

    // Загрузка файла
    @PostMapping
    public ResponseEntity<String> uploadFile(
            @RequestParam("filename") String filename,
            @RequestParam("file") MultipartFile file) {
        fileService.uploadFile(filename, file);
        return ResponseEntity.status(HttpStatus.CREATED).body("File uploaded successfully");
    }

    // Скачивание файла
    @GetMapping
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename) {
        Resource fileResource = fileService.getFile(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(fileResource);
    }

    // Обновление файла
    @PutMapping
    public ResponseEntity<String> updateFile(
            @RequestParam("filename") String filename,
            @RequestBody Map<String, Object> fileData) {
        fileService.updateFile(filename, fileData);
        return ResponseEntity.ok("File updated successfully");
    }

    // Удаление файла
    @DeleteMapping
    public ResponseEntity<String> deleteFile(@RequestParam("filename") String filename) {
        fileService.deleteFile(filename);
        return ResponseEntity.ok("File deleted successfully");
    }
}