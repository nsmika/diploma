package ru.netology.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.model.User;
import ru.netology.service.AuthService;
import ru.netology.service.FileService;
import ru.netology.dto.FileDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthFileControllerTests {

    @Mock
    private AuthService authService;

    @Mock
    private FileService fileService;

    @InjectMocks
    private AuthController authController;

    @InjectMocks
    private FileController fileController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Тест для авторизации (/login)
    @Test
    public void testLoginSuccess() {
        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");

        when(authService.authenticate(anyString(), anyString())).thenReturn("valid-token");

        ResponseEntity<Map<String, Object>> response = authController.login(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("valid-token", response.getBody().get("auth-token"));
    }

    @Test
    public void testLoginBadCredentials() {
        User user = new User();
        user.setLogin("testuser");
        user.setPassword("wrongpassword");

        when(authService.authenticate(anyString(), anyString())).thenThrow(new RuntimeException("Bad credentials"));

        ResponseEntity<Map<String, Object>> response = authController.login(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad credentials", response.getBody().get("message"));
    }

    // Тест для выхода (/logout)
    @Test
    public void testLogoutSuccess() {
        doNothing().when(authService).logout(anyString());

        ResponseEntity<Map<String, String>> response = authController.logout("valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Выход выполнен", response.getBody().get("message"));
    }

    @Test
    public void testLogoutInvalidToken() {
        doThrow(new IllegalArgumentException("Invalid or expired token")).when(authService).logout(anyString());

        ResponseEntity<Map<String, String>> response = authController.logout("invalid-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid or expired token", response.getBody().get("message"));
    }

    // Тест для загрузки файла (/file POST)
    @Test
    public void testUploadFileSuccess() {
        MultipartFile file = mock(MultipartFile.class);
        doNothing().when(fileService).uploadFile(anyString(), any(MultipartFile.class));

        ResponseEntity<?> response = fileController.uploadFile("valid-token", "testfile.txt", file);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("File uploaded successfully", response.getBody());
    }

    @Test
    public void testUploadFileError() {
        MultipartFile file = mock(MultipartFile.class);
        doThrow(new RuntimeException("Error uploading file")).when(fileService).uploadFile(anyString(), any(MultipartFile.class));

        ResponseEntity<?> response = fileController.uploadFile("valid-token", "testfile.txt", file);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error uploading file", ((ru.netology.model.Error) response.getBody()).getError());
    }

    // Тест для получения списка файлов (/file/list GET)
    @Test
    public void testGetFilesSuccess() {
        FileDto fileDto = new FileDto("testfile.txt", 1024);
        when(fileService.getFiles(anyInt())).thenReturn(Collections.singletonList(fileDto));

        ResponseEntity<?> response = fileController.getFiles("valid-token", 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, ((List<?>) response.getBody()).size());
    }

    @Test
    public void testGetFilesError() {
        when(fileService.getFiles(anyInt())).thenThrow(new RuntimeException("Error getting file list"));

        ResponseEntity<?> response = fileController.getFiles("valid-token", 10);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error getting file list", ((ru.netology.model.Error) response.getBody()).getError());
    }
}
