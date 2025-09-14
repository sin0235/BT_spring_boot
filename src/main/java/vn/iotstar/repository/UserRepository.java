package vn.iotstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iotstar.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.password = :password AND u.isActive = true")
    Optional<User> findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    
    @Query("SELECT u FROM User u WHERE u.roleId = :roleId AND u.isActive = true ORDER BY u.fullName")
    List<User> findByRoleId(@Param("roleId") Integer roleId);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true ORDER BY u.createdAt DESC")
    List<User> findAllActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.roleId = :roleId AND u.isActive = true")
    Long countByRoleId(@Param("roleId") Integer roleId);
    
    @Modifying
    @Query("UPDATE User u SET u.isActive = false WHERE u.userId = :userId")
    int deactivateUser(@Param("userId") Integer userId);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY u.createdAt DESC")
    List<User> searchByKeyword(@Param("keyword") String keyword);
    
    // Enhanced search with filters for admin
    @Query("SELECT u FROM User u WHERE " +
           "(:keyword IS NULL OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:roleId IS NULL OR u.roleId = :roleId) AND " +
           "(:isActive IS NULL OR u.isActive = :isActive) " +
           "ORDER BY u.createdAt DESC")
    List<User> adminSearchUsers(@Param("keyword") String keyword, @Param("roleId") Integer roleId, @Param("isActive") Boolean isActive);
    
    // Paginated search
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY u.createdAt DESC")
    Page<User> searchByKeywordPaginated(@Param("keyword") String keyword, Pageable pageable);
    
    // Additional statistics queries
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    Long countActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = false")
    Long countInactiveUsers();
    
    // Users with category counts
    @Query("SELECT u, COUNT(c) as categoryCount FROM User u " +
           "LEFT JOIN Category c ON u.userId = c.userId " +
           "GROUP BY u.userId " +
           "ORDER BY categoryCount DESC, u.fullName")
    List<Object[]> findUsersWithCategoryCount();
    
    // Recent users
    @Query("SELECT u FROM User u WHERE u.isActive = true ORDER BY u.createdAt DESC")
    List<User> findRecentActiveUsers(Pageable pageable);
}