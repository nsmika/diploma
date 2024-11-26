package ru.netology.model;


import jakarta.persistence.*;

@Entity
@Table(name = "files", schema = "cloud")
public class File {
    public File() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String filepath;
    private long filesize;

    public File(String filename, String filepath, long filesize) {
        this.filename = filename;
        this.filepath = filepath;
        this.filesize = filesize;
    }

    public Long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }
}

