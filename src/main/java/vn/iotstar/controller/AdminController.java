package vn.iotstar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.iotstar.entity.User;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping("/home")
    public String adminHome(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        // Add statistics for admin dashboard
        long totalUsers = userService.findAll().size();
        long totalCategories = categoryService.findAll().size();
        
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("user", user);
        
        return "admin/home";
    }
    
    @GetMapping("/users-overview")
    public String manageUsers(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }
    
    @GetMapping("/categories")
    public String manageAllCategories(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("categories", categoryService.findAll());
        return "admin/categories";
    }
}