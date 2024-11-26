package ru.netology.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.exception.UnauthorizedException;
import ru.netology.security.JwtTokenProvider;
import ru.netology.service.FileService;
import ru.netology.dto.FileDto;

import java.util.List;
import java.util.Map;

@RestController
public class FileController {

    private final FileService fileService;
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileDto>> getFiles(@RequestHeader("auth-token") String authToken,
                                                  @RequestParam("limit") int limit) {
        if (!jwtTokenProvider.validateToken(authToken)) {
            throw new UnauthorizedException("Неверный или истекший токен");
        }
        List<FileDto> files = fileService.getFiles(limit);
        return ResponseEntity.ok(files);
    }



    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestParam("filename") String filename,
                                             @RequestParam("file") MultipartFile file) {
        try {
            fileService.uploadFile(filename, file);
            return ResponseEntity.status(HttpStatus.CREATED).body("Файл успешно загружен");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при загрузке файла: " + e.getMessage());
        }
    }


    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename) {
        Resource fileResource = fileService.getFile(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(fileResource);
    }

    @PutMapping("/file")
    public ResponseEntity<String> updateFile(
            @RequestParam("filename") String filename,
            @RequestBody Map<String, Object> fileData) {
        fileService.updateFile(filename, fileData);
        return ResponseEntity.ok("File updated successfully");
    }

    @DeleteMapping("/file")
    public ResponseEntity<String> deleteFile(@RequestParam("filename") String filename) {
        fileService.deleteFile(filename);
        return ResponseEntity.ok("File deleted successfully");
    }
}
