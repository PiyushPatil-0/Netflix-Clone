package com.netflix.clone.controller;

import com.netflix.clone.service.FileUploadService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/upload/video")
    public ResponseEntity<Map<String,String>> uploadVideo(@RequestParam("file")MultipartFile file){
        String uuid = fileUploadService.storeVideoFile(file);
        return ResponseEntity.ok(buildFloatResponse(uuid,file));
    }

    @PostMapping("/upload/image")
    public ResponseEntity<Map<String,String>> uploadImage(@RequestParam("file")MultipartFile file){
        String uuid = fileUploadService.storeImageFile(file);
        return ResponseEntity.ok(buildFloatResponse(uuid,file));
    }

    private Map<String, String> buildFloatResponse(String uuid, MultipartFile file) {
        Map<String,String> response = new HashMap<>();
        response.put("uuid", uuid);
        response.put("filename", file.getOriginalFilename());
        response.put("size", String.valueOf(file.getSize()));
        return response;
    }

    @GetMapping("/video/{uuid}")
    public ResponseEntity<Resource> serveVideo(
            @PathVariable String uuid,
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            @RequestHeader(value = "token", required = false) String tokenParam){
        return fileUploadService.serveVideo(uuid, rangeHeader);
    }

    @GetMapping("/image/{uuid}")
    public ResponseEntity<Resource> serveImage(@PathVariable String uuid){
        return fileUploadService.serveImage(uuid);
    }
}
