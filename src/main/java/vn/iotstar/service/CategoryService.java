package vn.iotstar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.Category;
import vn.iotstar.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
    
    public Optional<Category> findById(Integer id) {
        return categoryRepository.findById(id);
    }
    
    public List<Category> findByUserId(Integer userId) {
        return categoryRepository.findByUserId(userId);
    }
    
    public List<Category> searchByName(String name) {
        return categoryRepository.findByCatenameContaining(name);
    }
    
    public List<Category> searchByUserAndName(Integer userId, String name) {
        return categoryRepository.findByUserIdAndCatenameContaining(userId, name);
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
}