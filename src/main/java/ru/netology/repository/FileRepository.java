package ru.netology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.model.File;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    // Поиск файла по имени и email пользователя
    Optional<File> findByFilenameAndUserEmail(String filename, String userEmail);

    // Получение всех файлов для конкретного пользователя
    List<File> findAllByUserEmail(String userEmail);

    // Поиск файла по имени
    Optional<File> findByFilename(String filename);
}
