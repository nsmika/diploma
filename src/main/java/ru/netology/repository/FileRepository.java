package ru.netology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.model.File;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByFilename(String filename);

    void deleteByFilename(String filename);
}
