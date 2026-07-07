package com.netflix.clone.controller;

import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.dto.response.PageResponse;
import com.netflix.clone.dto.response.VideoResponse;
import com.netflix.clone.service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    @PostMapping("/{videoId}")
    public ResponseEntity<MessageResponse> addToWatchlist(@PathVariable Long videoId, Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(watchlistService.addToWatchlist(email, videoId));
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<MessageResponse> removeFromWatchlist(@PathVariable Long videoId, Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(watchlistService.removeFromWatchlist(email, videoId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<VideoResponse>> getWatchlist(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10")int size,
            @RequestParam(required = false)String search,
            Authentication authentication){

        String email = authentication.getName();

        PageResponse<VideoResponse>response = watchlistService.getWatchlistPaginated(email, page, size, search);
        return ResponseEntity.ok(response);
    }
}
