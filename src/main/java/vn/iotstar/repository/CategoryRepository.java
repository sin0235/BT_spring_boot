package vn.iotstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Video;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    // Basic relationship queries
    List<Category> findByUserId(Integer userId);
    
    @Query("SELECT c FROM Category c ORDER BY c.createdAt DESC")
    List<Category> findAllOrderByCreatedAtDesc();
    
    @Query("SELECT c FROM Category c WHERE c.userId = :userId ORDER BY c.createdAt DESC")
    List<Category> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);
    
    // Enhanced search methods with JPA 3.0
    @Query("SELECT c FROM Category c WHERE LOWER(c.catename) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY c.catename")
    List<Category> findByCatenameContaining(@Param("name") String name);
    
    @Query("SELECT c FROM Category c WHERE c.userId = :userId AND LOWER(c.catename) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY c.catename")
    List<Category> findByUserIdAndCatenameContaining(@Param("userId") Integer userId, @Param("name") String name);
    
    // Advanced search for admin
    @Query("SELECT c FROM Category c WHERE " +
           "(:keyword IS NULL OR " +
           "LOWER(c.catename) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:userId IS NULL OR c.userId = :userId) " +
           "ORDER BY c.createdAt DESC")
    List<Category> adminSearchCategories(@Param("keyword") String keyword, @Param("userId") Integer userId);
    
    // Paginated search
    @Query("SELECT c FROM Category c WHERE LOWER(c.catename) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY c.createdAt DESC")
    Page<Category> searchByKeywordPaginated(@Param("keyword") String keyword, Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(c) FROM Category c WHERE c.userId = :userId")
    Long countByUserId(@Param("userId") Integer userId);
    
    @Query("SELECT COUNT(c) FROM Category c")
    Long countAllCategories();
    
    // Check for duplicates
    @Query("SELECT c FROM Category c WHERE LOWER(c.catename) = LOWER(:catename) AND c.userId = :userId")
    Optional<Category> findByNameAndUserId(@Param("catename") String catename, @Param("userId") Integer userId);
    
    @Query("SELECT c FROM Category c WHERE LOWER(c.catename) = LOWER(:catename) AND c.userId = :userId AND c.cateid != :categoryId")
    Optional<Category> findByNameAndUserIdExcludingId(@Param("catename") String catename, @Param("userId") Integer userId, @Param("categoryId") Integer categoryId);
    
    boolean existsByCatenameAndUserId(String catename, Integer userId);
    
    // Popular categories (based on video count)
    @Query("SELECT c FROM Category c LEFT JOIN Video v ON c.cateid = v.categoryId " +
           "GROUP BY c.cateid, c.catename, c.userId, c.createdAt " +
           "ORDER BY COUNT(v) DESC")
    List<Category> findPopularCategories(Pageable pageable);
    
    // Categories with video counts for admin dashboard
    @Query("SELECT c, COUNT(v) as videoCount FROM Category c " +
           "LEFT JOIN Video v ON c.cateid = v.categoryId AND v.isActive = true " +
           "GROUP BY c.cateid " +
           "ORDER BY videoCount DESC, c.catename")
    List<Object[]> findCategoriesWithVideoCount();
    
    // Recent categories
    @Query("SELECT c FROM Category c ORDER BY c.createdAt DESC")
    List<Category> findRecentCategories(Pageable pageable);
    
    // Categories by specific user with video counts
    @Query("SELECT c, COUNT(v) as videoCount FROM Category c " +
           "LEFT JOIN Video v ON c.cateid = v.categoryId AND v.isActive = true " +
           "WHERE c.userId = :userId " +
           "GROUP BY c.cateid " +
           "ORDER BY videoCount DESC, c.catename")
    List<Object[]> findUserCategoriesWithVideoCount(@Param("userId") Integer userId);
}