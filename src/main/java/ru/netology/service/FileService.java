package ru.netology.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.model.File;
import ru.netology.repository.FileRepository;
import ru.netology.security.JwtTokenProvider;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public FileService(FileRepository fileRepository, JwtTokenProvider jwtTokenProvider) {
        this.fileRepository = fileRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public void uploadFile(String authToken, String filename, byte[] content) {
        String userEmail = jwtTokenProvider.getUserEmailFromToken(authToken);

        File file = new File();
        file.setFilename(filename);
        file.setContent(content);
        file.setSize(content.length);
        file.setUserEmail(userEmail);
        fileRepository.save(file);
    }

    public byte[] getFile(String authToken, String filename) {
        String userEmail = jwtTokenProvider.getUserEmailFromToken(authToken);

        Optional<File> fileOptional = fileRepository.findByFilenameAndUserEmail(filename, userEmail);
        return fileOptional.map(File::getContent).orElse(null);
    }

    @Transactional
    public boolean deleteFile(String authToken, String filename) {
        String userEmail = jwtTokenProvider.getUserEmailFromToken(authToken);

        Optional<File> fileOptional = fileRepository.findByFilenameAndUserEmail(filename, userEmail);
        if (fileOptional.isPresent()) {
            fileRepository.delete(fileOptional.get());
            return true;
        }
        return false;
    }

    public List<FileInfo> listFiles(String authToken) {
        String userEmail = jwtTokenProvider.getUserEmailFromToken(authToken);

        return fileRepository.findAllByUserEmail(userEmail).stream()
                .map(file -> new FileInfo(file.getFilename(), file.getSize()))
                .collect(Collectors.toList());
    }

    public static class FileInfo {
        private final String filename;
        private final long size;

        public FileInfo(String filename, long size) {
            this.filename = filename;
            this.size = size;
        }

        public String getFilename() {
            return filename;
        }

        public long getSize() {
            return size;
        }
    }
}