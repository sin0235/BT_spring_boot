package vn.iotstar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.User;
import vn.iotstar.service.CategoryService;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    private final String UPLOAD_DIR = "uploads/";
    
    @GetMapping
    public String listCategories(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        List<Category> categories = categoryService.findByUserId(user.getUserId());
        model.addAttribute("categories", categories);
        return "category-list";
    }
    
    @GetMapping("/new")
    public String newCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "category-form";
    }
    
    @GetMapping("/edit/{id}")
    public String editCategoryForm(@PathVariable Integer id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        Optional<Category> categoryOpt = categoryService.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            // Check if user owns this category
            if (category.getUserId().equals(user.getUserId())) {
                model.addAttribute("category", category);
                return "category-form";
            }
        }
        return "redirect:/categories";
    }
    
    @PostMapping("/save")
    public String saveCategory(@ModelAttribute Category category,
                              @RequestParam(value = "iconFile", required = false) MultipartFile iconFile,
                              HttpSession session) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        category.setUserId(user.getUserId());
        
        // Handle file upload
        if (iconFile != null && !iconFile.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + iconFile.getOriginalFilename();
                String uploadPath = System.getProperty("user.dir") + File.separator + UPLOAD_DIR;
                
                // Create upload directory if it doesn't exist
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                
                Path filePath = Paths.get(uploadPath + fileName);
                Files.write(filePath, iconFile.getBytes());
                
                category.setIconFilename(fileName);
                category.setIconPath(UPLOAD_DIR + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        categoryService.save(category);
        return "redirect:/categories";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Integer id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        Optional<Category> categoryOpt = categoryService.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            // Check if user owns this category
            if (category.getUserId().equals(user.getUserId())) {
                categoryService.deleteById(id);
            }
        }
        return "redirect:/categories";
    }
    
    @GetMapping("/search")
    public String searchCategories(@RequestParam String keyword, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        List<Category> categories = categoryService.searchByUserAndName(user.getUserId(), keyword);
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
        return "category-list";
    }
}