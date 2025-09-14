package vn.iotstar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Basic CRUD operations
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    public List<User> findAllActive() {
        return userRepository.findAllActiveUsers();
    }
    
    public List<User> findByRole(Integer roleId) {
        return userRepository.findByRoleId(roleId);
    }
    
    public Long countByRole(Integer roleId) {
        return userRepository.countByRoleId(roleId);
    }
    
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
    
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }
    
    public boolean existsById(Integer id) {
        return userRepository.existsById(id);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }
    
    // Enhanced search methods
    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        return userRepository.searchByKeyword(keyword.trim());
    }
    
    public List<User> adminSearchUsers(String keyword, Integer roleId, Boolean isActive) {
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return userRepository.adminSearchUsers(searchKeyword, roleId, isActive);
    }
    
    public Page<User> searchByKeywordPaginated(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findAll(pageable);
        }
        return userRepository.searchByKeywordPaginated(keyword.trim(), pageable);
    }
    
    // Statistics
    public Long countActiveUsers() {
        return userRepository.countActiveUsers();
    }
    
    public Long countInactiveUsers() {
        return userRepository.countInactiveUsers();
    }
    
    public Long getTotalUsers() {
        return userRepository.count();
    }
    
    // Users with category counts
    public List<Object[]> findUsersWithCategoryCount() {
        return userRepository.findUsersWithCategoryCount();
    }
    
    // Recent users
    public List<User> findRecentActiveUsers(Pageable pageable) {
        return userRepository.findRecentActiveUsers(pageable);
    }
    
    // User management operations
    @Transactional
    public boolean deactivateUser(Integer userId) {
        return userRepository.deactivateUser(userId) > 0;
    }
    
    public void activateUser(Integer userId) {
        Optional<User> userOpt = findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(true);
            save(user);
        }
    }
    
    public void toggleUserStatus(Integer userId) {
        Optional<User> userOpt = findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(!user.isActive());
            save(user);
        }
    }
    
    // Validation methods
    public boolean isDuplicateUsername(String username, Integer excludeUserId) {
        Optional<User> existingUser = findByUsername(username);
        if (existingUser.isPresent()) {
            return excludeUserId == null || existingUser.get().getUserId() != excludeUserId.intValue();
        }
        return false;
    }
    
    public boolean isDuplicateEmail(String email, Integer excludeUserId) {
        Optional<User> existingUser = findByEmail(email);
        if (existingUser.isPresent()) {
            return excludeUserId == null || existingUser.get().getUserId() != excludeUserId.intValue();
        }
        return false;
    }
    
    // Business logic methods
    public User createUser(User user) {
        // Validate unique username and email
        if (existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Set default values
        if (user.getActive() == null) {
            user.setActive(true);
        }
        if (user.getRoleId() == 0) {
            user.setRoleId(1); // Default to User role
        }
        
        return save(user);
    }
    
    public User updateUser(User user) {
        // Validate unique username and email (excluding current user)
        if (isDuplicateUsername(user.getUsername(), user.getUserId())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (isDuplicateEmail(user.getEmail(), user.getUserId())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        return save(user);
    }
    
    // Role-based access methods for admin
    public List<User> getUsersForAdmin() {
        return findAll();
    }
    
    public List<User> getActiveUsersForAdmin() {
        return findAllActive();
    }
    
    public List<User> getUsersByRole(Integer roleId) {
        return findByRole(roleId);
    }
    
    // Search methods for admin with different filters
    public List<User> adminSearchAllUsers(String keyword) {
        return adminSearchUsers(keyword, null, null);
    }
    
    public List<User> adminSearchActiveUsers(String keyword) {
        return adminSearchUsers(keyword, null, true);
    }
    
    public List<User> adminSearchUsersByRole(String keyword, Integer roleId) {
        return adminSearchUsers(keyword, roleId, null);
    }
}