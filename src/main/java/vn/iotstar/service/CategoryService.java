package vn.iotstar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.entity.Category;
import vn.iotstar.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    // Basic CRUD operations
    public List<Category> findAll() {
        return categoryRepository.findAllOrderByCreatedAtDesc();
    }
    
    public Optional<Category> findById(Integer id) {
        return categoryRepository.findById(id);
    }
    
    public List<Category> findByUserId(Integer userId) {
        return categoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public Category save(Category category) {
        return categoryRepository.save(category);
    }
    
    public void deleteById(Integer id) {
        categoryRepository.deleteById(id);
    }
    
    public boolean existsById(Integer id) {
        return categoryRepository.existsById(id);
    }
    
    // Enhanced search methods
    public List<Category> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }
        return categoryRepository.findByCatenameContaining(name.trim());
    }
    
    public List<Category> searchByUserAndName(Integer userId, String name) {
        if (name == null || name.trim().isEmpty()) {
            return findByUserId(userId);
        }
        return categoryRepository.findByUserIdAndCatenameContaining(userId, name.trim());
    }
    
    // Admin search with advanced filters
    public List<Category> adminSearchCategories(String keyword, Integer userId) {
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return categoryRepository.adminSearchCategories(searchKeyword, userId);
    }
    
    // Paginated search
    public Page<Category> searchByKeywordPaginated(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return categoryRepository.findAll(pageable);
        }
        return categoryRepository.searchByKeywordPaginated(keyword.trim(), pageable);
    }
    
    // Statistics
    public Long countByUserId(Integer userId) {
        return categoryRepository.countByUserId(userId);
    }
    
    public Long countAllCategories() {
        return categoryRepository.countAllCategories();
    }
    
    // Validation methods
    public boolean existsByNameAndUserId(String catename, Integer userId) {
        return categoryRepository.existsByCatenameAndUserId(catename, userId);
    }
    
    public Optional<Category> findByNameAndUserId(String catename, Integer userId) {
        return categoryRepository.findByNameAndUserId(catename, userId);
    }
    
    public boolean isDuplicateName(String catename, Integer userId, Integer excludeId) {
        if (excludeId != null) {
            return categoryRepository.findByNameAndUserIdExcludingId(catename, userId, excludeId).isPresent();
        }
        return categoryRepository.findByNameAndUserId(catename, userId).isPresent();
    }
    
    // Popular and recent categories
    public List<Category> findPopularCategories(Pageable pageable) {
        return categoryRepository.findPopularCategories(pageable);
    }
    
    public List<Category> findRecentCategories(Pageable pageable) {
        return categoryRepository.findRecentCategories(pageable);
    }
    
    // Categories with video counts
    public List<Object[]> findCategoriesWithVideoCount() {
        return categoryRepository.findCategoriesWithVideoCount();
    }
    
    public List<Object[]> findUserCategoriesWithVideoCount(Integer userId) {
        return categoryRepository.findUserCategoriesWithVideoCount(userId);
    }
    
    // Business logic methods
    public Category createCategory(Category category) {
        // Validate unique name per user
        if (existsByNameAndUserId(category.getCatename(), category.getUserId())) {
            throw new IllegalArgumentException("Category name already exists for this user");
        }
        
        return save(category);
    }
    
    public Category updateCategory(Category category) {
        // Validate unique name per user (excluding current category)
        if (isDuplicateName(category.getCatename(), category.getUserId(), category.getCateid())) {
            throw new IllegalArgumentException("Category name already exists for this user");
        }
        
        return save(category);
    }
    
    // Role-based access methods
    public List<Category> getCategoriesForAdmin() {
        return findAll();
    }
    
    public List<Category> getCategoriesForManager(Integer userId) {
        return findByUserId(userId);
    }
    
    public List<Category> getCategoriesForUser() {
        return findAll(); // Users can see all categories but can't modify them
    }
    
    // Search methods for different roles
    public List<Category> adminSearch(String keyword, Integer filterUserId) {
        return adminSearchCategories(keyword, filterUserId);
    }
    
    public List<Category> managerSearch(Integer userId, String keyword) {
        return searchByUserAndName(userId, keyword);
    }
    
    public List<Category> userSearch(String keyword) {
        return searchByName(keyword);
    }
}