package com.example.secondhand.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    String upload(MultipartFile file);

    List<String> uploadMultiple(List<MultipartFile> files);

    void delete(String fileName);
}
