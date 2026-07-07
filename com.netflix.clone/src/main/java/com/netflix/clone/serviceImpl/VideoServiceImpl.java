package com.netflix.clone.serviceImpl;

import com.netflix.clone.dao.UserRepository;
import com.netflix.clone.dao.VideoRepository;
import com.netflix.clone.dto.request.VideoRequest;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.dto.response.PageResponse;
import com.netflix.clone.dto.response.VideoResponse;
import com.netflix.clone.dto.response.VideoStatsResponse;
import com.netflix.clone.entity.Video;
import com.netflix.clone.service.VideoService;
import com.netflix.clone.util.PaginationUtils;
import com.netflix.clone.util.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceUtils serviceUtils;

    @Override
    public MessageResponse createVideoByAdmin(VideoRequest videoRequest) {
        Video video = new Video();
        video.setTitle(videoRequest.getTitle());
        video.setDescription(videoRequest.getDescription());
        video.setYear(videoRequest.getYear());
        video.setRating(videoRequest.getRating());
        video.setDuration(videoRequest.getDuration());
        video.setSrcUuid(videoRequest.getSrc());
        video.setPosterUuid(videoRequest.getPoster());
        video.setPublished(videoRequest.isPublished());
        video.setCategories(videoRequest.getCategories() != null ? videoRequest.getCategories(): List.of());
        videoRepository.save(video);

        return new MessageResponse("Video created successfully");
    }

    @Override
    public PageResponse<VideoResponse> getAllAdminVideos(int page, int size, String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size,"id");
        Page<Video> videoPage;

        if (search != null && !search.trim().isEmpty()){
            videoPage = videoRepository.searchVideos(search.trim(), pageable);
        }else {
            videoPage = videoRepository.findAll(pageable);
        }
        return PaginationUtils.toPageResponse(videoPage, VideoResponse::fromEntity);
    }

    @Override
    public MessageResponse updateVideoByAdmin(Long id, VideoRequest videoRequest) {
        Video video = new Video();
        video.setId(id);
        video.setTitle(videoRequest.getTitle());
        video.setDescription(videoRequest.getDescription());
        video.setYear(videoRequest.getYear());
        video.setRating(videoRequest.getRating());
        video.setDuration(videoRequest.getDuration());
        video.setSrcUuid(videoRequest.getSrc());
        video.setPosterUuid(videoRequest.getPoster());
        video.setPublished(videoRequest.isPublished());
        video.setCategories(videoRequest.getCategories() != null ? videoRequest.getCategories(): List.of());
        videoRepository.save(video);

        return new MessageResponse("Video updated successfully");
    }

    @Override
    public MessageResponse deleteVideoByAdmin(Long id) {
        if (!videoRepository.existsById(id)){
            throw new IllegalArgumentException("Video not found with id: " + id);
        }
        videoRepository.deleteById(id);
        return new MessageResponse("Video deleted successfully");
    }

    @Override
    public MessageResponse toggleVideoPublishStatusByAdmin(Long id, boolean status) {
        Video video = serviceUtils.getVideoByIdOrThrow(id);
        video.setPublished(status);
        videoRepository.save(video);

        return new MessageResponse("Video publish status updated successfully");
    }

    @Override
    public VideoStatsResponse getAdminStats() {
        long totalVideos = videoRepository.count();
        long publishedVideos = videoRepository.countPublishedVideos();
        long totalDuration = videoRepository.getTotalDuration();

        return new VideoStatsResponse(totalVideos, publishedVideos, totalDuration);
    }

    @Override
    public PageResponse<VideoResponse> getPublishedVideos(int page, int size, String search, String email) {
        Pageable pageable = PaginationUtils.createPageRequest(page,size,"id");
        Page<Video> videoPage;

        if (search != null && !search.trim().isEmpty()){
            videoPage = videoRepository.searchPublishedVideos(search.trim(), pageable);
        }else {
            videoPage = videoRepository.findPublishedVideos(pageable);
        }
        List<Video> videos = videoPage.getContent();

        Set<Long> watchListIds = Set.of();
        if (!videos.isEmpty()){
            try {
                List<Long> videoIds = videos.stream().map(Video::getId).toList();
                watchListIds = userRepository.findWatchlistVideoIds(email, videoIds);
            } catch (Exception ex) {
                watchListIds = Set.of();
            }
        }
        Set<Long> finalWatchlistIds = watchListIds;
        videos.forEach(video -> video.setIsInWatchList(finalWatchlistIds.contains(video.getId())));

        List<VideoResponse> videoResponses = videos.stream().map(VideoResponse::fromEntity).toList();
        return PaginationUtils.toPageResponse(videoPage,videoResponses);

    }

    @Override
    public List<VideoResponse> getFeaturedVideos() {
        Pageable pageable = PageRequest.of(0, 5);
            List<Video> videos = videoRepository.findRandomPublishedVideos(pageable);

            return videos.stream().map(VideoResponse::fromEntity).toList();
    }
}
