package vn.iotstar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.iotstar.entity.User;
import vn.iotstar.service.CategoryService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping("/home")
    public String userHome(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 1) {
            return "redirect:/auth/login";
        }
        
        // Add user's categories count
        long userCategories = categoryService.findByUserId(user.getUserId()).size();
        
        // Add all categories for user to view
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("userCategories", userCategories);
        model.addAttribute("user", user);
        
        return "user/home";
    }
}