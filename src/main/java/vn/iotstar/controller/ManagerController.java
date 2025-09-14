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
@RequestMapping("/manager")
public class ManagerController {
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping("/home")
    public String managerHome(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 2) {
            return "redirect:/auth/login";
        }
        
        // Add statistics for manager dashboard
        long userCategories = categoryService.findByUserId(user.getUserId()).size();
        
        model.addAttribute("userCategories", userCategories);
        model.addAttribute("user", user);
        
        return "manager/home";
    }
}