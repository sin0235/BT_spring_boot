package vn.iotstar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.entity.Video;
import vn.iotstar.repository.VideoRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VideoService {
    
    @Autowired
    private VideoRepository videoRepository;
    
    // Basic CRUD operations
    public List<Video> findAll() {
        return videoRepository.findAll();
    }
    
    public Optional<Video> findById(Integer id) {
        return videoRepository.findById(id);
    }
    
    public Video save(Video video) {
        return videoRepository.save(video);
    }
    
    public void deleteById(Integer id) {
        videoRepository.deleteById(id);
    }
    
    public boolean existsById(Integer id) {
        return videoRepository.existsById(id);
    }
    
    // Find by relationships
    public List<Video> findByUserId(Integer userId) {
        return videoRepository.findByUserId(userId);
    }
    
    public List<Video> findByCategoryId(Integer categoryId) {
        return videoRepository.findByCategoryId(categoryId);
    }
    
    public List<Video> findByUserIdAndCategoryId(Integer userId, Integer categoryId) {
        return videoRepository.findByUserIdAndCategoryId(userId, categoryId);
    }
    
    // Active and public videos
    public List<Video> findAllActiveVideos() {
        return videoRepository.findAllActiveVideos();
    }
    
    public List<Video> findAllPublicVideos() {
        return videoRepository.findAllPublicVideos();
    }
    
    // Search operations
    public List<Video> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        return videoRepository.searchByKeyword(keyword.trim());
    }
    
    public List<Video> searchActiveByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllActiveVideos();
        }
        return videoRepository.searchActiveByKeyword(keyword.trim());
    }
    
    public List<Video> searchByUserAndKeyword(Integer userId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findByUserId(userId);
        }
        return videoRepository.searchByUserAndKeyword(userId, keyword.trim());
    }
    
    public List<Video> searchByCategoryAndKeyword(Integer categoryId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findByCategoryId(categoryId);
        }
        return videoRepository.searchByCategoryAndKeyword(categoryId, keyword.trim());
    }
    
    // Advanced search with filters
    public List<Video> searchWithFilters(Integer userId, Integer categoryId, 
                                       Boolean isActive, Boolean isPublic, String keyword) {
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return videoRepository.searchWithFilters(userId, categoryId, isActive, isPublic, searchKeyword);
    }
    
    // Paginated search
    public Page<Video> searchByKeywordPaginated(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return videoRepository.findAll(pageable);
        }
        return videoRepository.searchByKeywordPaginated(keyword.trim(), pageable);
    }
    
    // Statistics methods
    public Long countActiveVideosByUser(Integer userId) {
        return videoRepository.countActiveVideosByUser(userId);
    }
    
    public Long countActiveVideosByCategory(Integer categoryId) {
        return videoRepository.countActiveVideosByCategory(categoryId);
    }
    
    public Long getTotalViewsByUser(Integer userId) {
        Long views = videoRepository.getTotalViewsByUser(userId);
        return views != null ? views : 0L;
    }
    
    public Long countPublicVideos() {
        return videoRepository.countPublicVideos();
    }
    
    public Long getTotalVideos() {
        return videoRepository.count();
    }
    
    public Long getTotalActiveVideos() {
        return (long) findAllActiveVideos().size();
    }
    
    // Top videos
    public List<Video> findTopVideosByViews(Pageable pageable) {
        return videoRepository.findTopVideosByViews(pageable);
    }
    
    public List<Video> findTopVideosByCategoryAndViews(Integer categoryId, Pageable pageable) {
        return videoRepository.findTopVideosByCategoryAndViews(categoryId, pageable);
    }
    
    // Recent videos
    public List<Video> findRecentVideos(Pageable pageable) {
        return videoRepository.findRecentVideos(pageable);
    }
    
    public List<Video> findRecentVideosByUser(Integer userId, Pageable pageable) {
        return videoRepository.findRecentVideosByUser(userId, pageable);
    }
    
    // Update operations
    public void incrementViews(Integer videoId) {
        videoRepository.incrementViews(videoId);
    }
    
    public void updateActiveStatus(Integer videoId, Boolean isActive) {
        videoRepository.updateActiveStatus(videoId, isActive);
    }
    
    public void updatePublicStatus(Integer videoId, Boolean isPublic) {
        videoRepository.updatePublicStatus(videoId, isPublic);
    }
    
    public void deactivateVideosByUser(Integer userId) {
        videoRepository.deactivateVideosByUser(userId);
    }
    
    public void deactivateVideosByCategory(Integer categoryId) {
        videoRepository.deactivateVideosByCategory(categoryId);
    }
    
    // Validation methods
    public boolean existsByTitleAndUserId(String title, Integer userId) {
        return videoRepository.existsByTitleAndUserId(title, userId);
    }
    
    public boolean existsByVideoUrlAndUserId(String videoUrl, Integer userId) {
        return videoRepository.existsByVideoUrlAndUserId(videoUrl, userId);
    }
    
    // Business logic methods
    public Video createVideo(Video video) {
        // Set default values
        if (video.getViews() == null) {
            video.setViews(0L);
        }
        if (video.getActive() == null) {
            video.setActive(true);
        }
        if (video.getPublic() == null) {
            video.setPublic(true);
        }
        
        return save(video);
    }
    
    public Video updateVideo(Video video) {
        return save(video);
    }
    
    public void softDeleteVideo(Integer videoId) {
        updateActiveStatus(videoId, false);
    }
    
    public void restoreVideo(Integer videoId) {
        updateActiveStatus(videoId, true);
    }
    
    public void togglePublicStatus(Integer videoId) {
        Optional<Video> videoOpt = findById(videoId);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            updatePublicStatus(videoId, !video.isPublic());
        }
    }
    
    // Search for admin (includes all videos)
    public List<Video> adminSearchVideos(String keyword) {
        return searchByKeyword(keyword);
    }
    
    // Search for manager (only their videos)
    public List<Video> managerSearchVideos(Integer userId, String keyword) {
        return searchByUserAndKeyword(userId, keyword);
    }
    
    // Search for user (only public videos)
    public List<Video> userSearchVideos(String keyword) {
        return searchActiveByKeyword(keyword);
    }
    
    // Get videos for different roles
    public List<Video> getVideosForAdmin() {
        return findAll();
    }
    
    public List<Video> getVideosForManager(Integer userId) {
        return findByUserId(userId);
    }
    
    public List<Video> getVideosForUser() {
        return findAllPublicVideos();
    }
}
