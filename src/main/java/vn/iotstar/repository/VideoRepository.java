package vn.iotstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iotstar.entity.Video;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {
    
    // Basic findBy methods
    List<Video> findByUserId(Integer userId);
    
    List<Video> findByCategoryId(Integer categoryId);
    
    List<Video> findByUserIdAndCategoryId(Integer userId, Integer categoryId);
    
    @Query("SELECT v FROM Video v WHERE v.isActive = true ORDER BY v.createdAt DESC")
    List<Video> findAllActiveVideos();
    
    @Query("SELECT v FROM Video v WHERE v.isActive = true AND v.isPublic = true ORDER BY v.views DESC")
    List<Video> findAllPublicVideos();
    
    // Search methods with JPA 3.0 query syntax
    @Query("SELECT v FROM Video v WHERE " +
           "(LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY v.createdAt DESC")
    List<Video> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT v FROM Video v WHERE " +
           "(LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND v.isActive = true " +
           "ORDER BY v.createdAt DESC")
    List<Video> searchActiveByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT v FROM Video v WHERE " +
           "v.userId = :userId AND " +
           "(LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY v.createdAt DESC")
    List<Video> searchByUserAndKeyword(@Param("userId") Integer userId, @Param("keyword") String keyword);
    
    @Query("SELECT v FROM Video v WHERE " +
           "v.categoryId = :categoryId AND " +
           "(LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY v.createdAt DESC")
    List<Video> searchByCategoryAndKeyword(@Param("categoryId") Integer categoryId, @Param("keyword") String keyword);
    
    // Advanced search with multiple filters
    @Query("SELECT v FROM Video v WHERE " +
           "(:userId IS NULL OR v.userId = :userId) AND " +
           "(:categoryId IS NULL OR v.categoryId = :categoryId) AND " +
           "(:isActive IS NULL OR v.isActive = :isActive) AND " +
           "(:isPublic IS NULL OR v.isPublic = :isPublic) AND " +
           "(:keyword IS NULL OR " +
           "LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY v.createdAt DESC")
    List<Video> searchWithFilters(
        @Param("userId") Integer userId,
        @Param("categoryId") Integer categoryId,
        @Param("isActive") Boolean isActive,
        @Param("isPublic") Boolean isPublic,
        @Param("keyword") String keyword
    );
    
    // Paginated search
    @Query("SELECT v FROM Video v WHERE " +
           "(LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY v.createdAt DESC")
    Page<Video> searchByKeywordPaginated(@Param("keyword") String keyword, Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(v) FROM Video v WHERE v.userId = :userId AND v.isActive = true")
    Long countActiveVideosByUser(@Param("userId") Integer userId);
    
    @Query("SELECT COUNT(v) FROM Video v WHERE v.categoryId = :categoryId AND v.isActive = true")
    Long countActiveVideosByCategory(@Param("categoryId") Integer categoryId);
    
    @Query("SELECT SUM(v.views) FROM Video v WHERE v.userId = :userId AND v.isActive = true")
    Long getTotalViewsByUser(@Param("userId") Integer userId);
    
    @Query("SELECT COUNT(v) FROM Video v WHERE v.isActive = true AND v.isPublic = true")
    Long countPublicVideos();
    
    // Top videos queries
    @Query("SELECT v FROM Video v WHERE v.isActive = true AND v.isPublic = true ORDER BY v.views DESC")
    List<Video> findTopVideosByViews(Pageable pageable);
    
    @Query("SELECT v FROM Video v WHERE v.categoryId = :categoryId AND v.isActive = true ORDER BY v.views DESC")
    List<Video> findTopVideosByCategoryAndViews(@Param("categoryId") Integer categoryId, Pageable pageable);
    
    // Update methods
    @Modifying
    @Query("UPDATE Video v SET v.views = v.views + 1 WHERE v.videoId = :videoId")
    int incrementViews(@Param("videoId") Integer videoId);
    
    @Modifying
    @Query("UPDATE Video v SET v.isActive = :isActive WHERE v.videoId = :videoId")
    int updateActiveStatus(@Param("videoId") Integer videoId, @Param("isActive") Boolean isActive);
    
    @Modifying
    @Query("UPDATE Video v SET v.isPublic = :isPublic WHERE v.videoId = :videoId")
    int updatePublicStatus(@Param("videoId") Integer videoId, @Param("isPublic") Boolean isPublic);
    
    @Modifying
    @Query("UPDATE Video v SET v.isActive = false WHERE v.userId = :userId")
    int deactivateVideosByUser(@Param("userId") Integer userId);
    
    @Modifying
    @Query("UPDATE Video v SET v.isActive = false WHERE v.categoryId = :categoryId")
    int deactivateVideosByCategory(@Param("categoryId") Integer categoryId);
    
    // Check existence methods
    boolean existsByTitleAndUserId(String title, Integer userId);
    
    boolean existsByVideoUrlAndUserId(String videoUrl, Integer userId);
    
    // Recently added videos
    @Query("SELECT v FROM Video v WHERE v.isActive = true ORDER BY v.createdAt DESC")
    List<Video> findRecentVideos(Pageable pageable);
    
    @Query("SELECT v FROM Video v WHERE v.userId = :userId AND v.isActive = true ORDER BY v.createdAt DESC")
    List<Video> findRecentVideosByUser(@Param("userId") Integer userId, Pageable pageable);
}
