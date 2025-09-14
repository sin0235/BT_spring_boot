package vn.iotstar.repository;

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
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.fullName LIKE %:keyword% OR u.email LIKE %:keyword% ORDER BY u.createdAt DESC")
    List<User> searchByKeyword(@Param("keyword") String keyword);
}