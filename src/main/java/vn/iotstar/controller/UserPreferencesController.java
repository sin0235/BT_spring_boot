package vn.iotstar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.entity.User;
import vn.iotstar.entity.Category;
import vn.iotstar.service.UserService;
import vn.iotstar.service.CategoryService;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user/preferences")
public class UserPreferencesController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping
    public String userPreferences(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 1) { // User only
            return "redirect:/auth/login";
        }
        
        // Lấy thông tin user mới nhất
        Optional<User> currentUserOpt = userService.findById(user.getUserId());
        if (currentUserOpt.isPresent()) {
            model.addAttribute("user", currentUserOpt.get());
        } else {
            model.addAttribute("user", user);
        }
        
        return "user/preferences";
    }
    
    @GetMapping("/dashboard")
    public String userDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 1) {
            return "redirect:/auth/login";
        }
        
        // Thống kê cá nhân
        List<Category> userCategories = categoryService.findByUserId(user.getUserId());
        
        model.addAttribute("user", user);
        model.addAttribute("totalCategories", userCategories.size());
        model.addAttribute("recentCategories", userCategories.stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .limit(5)
                .toList());
        
        return "user/dashboard";
    }
    
    @GetMapping("/activity")
    public String userActivity(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 1) {
            return "redirect:/auth/login";
        }
        
        // Lấy hoạt động gần đây của user
        List<Category> userCategories = categoryService.findByUserId(user.getUserId());
        
        model.addAttribute("user", user);
        model.addAttribute("categories", userCategories);
        model.addAttribute("totalActivities", userCategories.size());
        
        return "user/activity";
    }
    
    @PostMapping("/update-notifications")
    public String updateNotificationSettings(@RequestParam(defaultValue = "false") boolean emailNotifications,
                                            @RequestParam(defaultValue = "false") boolean smsNotifications,
                                            @RequestParam(defaultValue = "false") boolean pushNotifications,
                                            HttpSession session,
                                            RedirectAttributes redirectAttributes) {
        
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null || sessionUser.getRoleId() != 1) {
            return "redirect:/auth/login";
        }
        
        try {
            Optional<User> currentUserOpt = userService.findById(sessionUser.getUserId());
            if (currentUserOpt.isPresent()) {
                User currentUser = currentUserOpt.get();
                
                // Lưu preferences vào field phone tạm thời (có thể tạo table riêng sau)
                String preferences = String.format("email:%s,sms:%s,push:%s", 
                    emailNotifications, smsNotifications, pushNotifications);
                currentUser.setPhone(preferences);
                currentUser.setUpdatedAt(LocalDateTime.now());
                
                User savedUser = userService.save(currentUser);
                session.setAttribute("user", savedUser);
                
                redirectAttributes.addFlashAttribute("success", "Cập nhật cài đặt thông báo thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin người dùng");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật cài đặt");
        }
        
        return "redirect:/user/preferences";
    }
    
    @GetMapping("/export-data")
    public String exportUserData(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 1) {
            return "redirect:/auth/login";
        }
        
        try {
            // Lấy tất cả dữ liệu của user
            List<Category> userCategories = categoryService.findByUserId(user.getUserId());
            
            // Tạo báo cáo dữ liệu (simplified)
            StringBuilder dataReport = new StringBuilder();
            dataReport.append("=== BÁO CÁO DỮ LIỆU CÁ NHÂN ===\n");
            dataReport.append("Người dùng: ").append(user.getFullName()).append("\n");
            dataReport.append("Email: ").append(user.getEmail()).append("\n");
            dataReport.append("Ngày tạo tài khoản: ").append(user.getCreatedAt()).append("\n");
            dataReport.append("Tổng số danh mục: ").append(userCategories.size()).append("\n\n");
            
            dataReport.append("=== DANH SÁCH DANH MỤC ===\n");
            for (Category category : userCategories) {
                dataReport.append("- ").append(category.getCatename())
                         .append(" (Tạo lúc: ").append(category.getCreatedAt()).append(")\n");
            }
            
            model.addAttribute("dataReport", dataReport.toString());
            model.addAttribute("user", user);
            
            redirectAttributes.addFlashAttribute("success", "Dữ liệu đã được xuất thành công!");
            return "user/export-data";
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xuất dữ liệu");
            return "redirect:/user/preferences";
        }
    }
    
    @PostMapping("/request-data-deletion")
    public String requestDataDeletion(@RequestParam String confirmPassword,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null || sessionUser.getRoleId() != 1) {
            return "redirect:/auth/login";
        }
        
        // Xác thực mật khẩu
        Optional<User> userOpt = userService.authenticate(sessionUser.getUsername(), confirmPassword);
        if (!userOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu không đúng");
            return "redirect:/user/preferences";
        }
        
        try {
            // Trong thực tế, đây sẽ là một quy trình phức tạp
            // Hiện tại chỉ đánh dấu tài khoản là inactive
            User currentUser = userOpt.get();
            currentUser.setActive(false);
            currentUser.setUpdatedAt(LocalDateTime.now());
            userService.save(currentUser);
            
            // Xóa session
            session.invalidate();
            
            redirectAttributes.addFlashAttribute("success", 
                "Yêu cầu xóa dữ liệu đã được gửi. Tài khoản đã được vô hiệu hóa.");
            return "redirect:/auth/login";
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xử lý yêu cầu");
            return "redirect:/user/preferences";
        }
    }
}
