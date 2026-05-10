package com.example.secondhand.controller;

import com.example.secondhand.dto.ResponseDTO;
import com.example.secondhand.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseDTO<String>> upload(@RequestParam("file") MultipartFile file) {
        String url = fileService.upload(file);
        return ResponseEntity.ok(ResponseDTO.success("上传成功", url));
    }

    @PostMapping("/upload/multiple")
    public ResponseEntity<ResponseDTO<List<String>>> uploadMultiple(@RequestParam("files") List<MultipartFile> files) {
        List<String> urls = fileService.uploadMultiple(files);
        return ResponseEntity.ok(ResponseDTO.success("上传成功", urls));
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<ResponseDTO<Void>> delete(@PathVariable String fileName) {
        fileService.delete(fileName);
        return ResponseEntity.ok(ResponseDTO.success("删除成功", null));
    }
}
