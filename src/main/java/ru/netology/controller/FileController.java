package ru.netology.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.service.FileService;
import ru.netology.service.FileService.FileInfo;

import java.util.List;

@RestController
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // Метод для загрузки файла с привязкой к пользователю
    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam String filename,
                                        @RequestBody byte[] fileContent) {
        fileService.uploadFile(authToken, filename, fileContent);
        return ResponseEntity.ok().build();
    }

    // Метод для получения содержимого файла
    @GetMapping("/file")
    public ResponseEntity<?> getFile(@RequestHeader("auth-token") String authToken,
                                     @RequestParam String filename) {
        byte[] file = fileService.getFile(authToken, filename);
        if (file != null) {
            return ResponseEntity.ok(file);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Метод для удаления файла
    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename) {
        boolean deleted = fileService.deleteFile(authToken, filename);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Метод для получения списка файлов с их размерами
    @GetMapping("/list")
    public ResponseEntity<List<FileInfo>> listFiles(@RequestHeader("auth-token") String authToken) {
        List<FileInfo> files = fileService.listFiles(authToken);
        return ResponseEntity.ok(files);
    }
}