package com.netflix.clone.controller;

import com.netflix.clone.dto.request.VideoRequest;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.dto.response.PageResponse;
import com.netflix.clone.dto.response.VideoResponse;
import com.netflix.clone.dto.response.VideoStatsResponse;
import com.netflix.clone.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<MessageResponse> createVideoByAdmin(@Valid @RequestBody VideoRequest videoRequest){
        return ResponseEntity.ok(videoService.createVideoByAdmin(videoRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<PageResponse<VideoResponse>> getAllAdminVideos(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10")int size,
            @RequestParam(required = false)String search){
        return ResponseEntity.ok(videoService.getAllAdminVideos(page,size,search));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<MessageResponse> updateVideoByAdmin(@PathVariable Long id, @Valid @RequestBody VideoRequest videoRequest){
        return ResponseEntity.ok(videoService.updateVideoByAdmin(id, videoRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<MessageResponse> deleteVideoByAdmin(@PathVariable Long id){
        return ResponseEntity.ok(videoService.deleteVideoByAdmin(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/{id}/publish")
    public ResponseEntity<MessageResponse> toggleVideoPublishStatusByAdmin(@PathVariable Long id, @RequestParam boolean value){
        return ResponseEntity.ok(videoService.toggleVideoPublishStatusByAdmin(id, value));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/stats")
    public ResponseEntity<VideoStatsResponse> getAdminStats(){
        return ResponseEntity.ok(videoService.getAdminStats());
    }

    @GetMapping("/published")
    public ResponseEntity<PageResponse<VideoResponse>> getPublishedVideos(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10")int size,
            @RequestParam(required = false)String search,
            Authentication authentication){
        String email = authentication.getName();
        PageResponse<VideoResponse> response = videoService.getPublishedVideos(page,size,search,email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<VideoResponse>> getFeaturedVideos(){
        List<VideoResponse> response = videoService.getFeaturedVideos();
        return ResponseEntity.ok(response);
    }
}
