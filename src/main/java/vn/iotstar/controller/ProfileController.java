package vn.iotstar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.entity.User;
import vn.iotstar.service.UserService;
import vn.iotstar.service.ImageService;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ImageService imageService;
    
    @GetMapping
    public String viewProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        // Lấy thông tin user mới nhất từ database
        Optional<User> currentUser = userService.findById(user.getUserId());
        if (currentUser.isPresent()) {
            model.addAttribute("user", currentUser.get());
        } else {
            model.addAttribute("user", user);
        }
        
        return "profile/view";
    }
    
    @GetMapping("/edit")
    public String editProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        // Lấy thông tin user mới nhất từ database
        Optional<User> currentUser = userService.findById(user.getUserId());
        if (currentUser.isPresent()) {
            model.addAttribute("user", currentUser.get());
        } else {
            model.addAttribute("user", user);
        }
        
        return "profile/edit";
    }
    
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute User user,
                               @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/auth/login";
        }
        
        // Bỏ validation tạm thời để test
        
        try {
            // Lấy thông tin user hiện tại từ database
            Optional<User> currentUserOpt = userService.findById(sessionUser.getUserId());
            if (!currentUserOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin người dùng");
                return "redirect:/profile";
            }
            
            User currentUser = currentUserOpt.get();
            
            // Cập nhật các thông tin được phép chỉnh sửa
            currentUser.setFullName(user.getFullName());
            currentUser.setEmail(user.getEmail());
            currentUser.setPhone(user.getPhone());
            
            // Xử lý upload ảnh đại diện
            if (avatarFile != null && !avatarFile.isEmpty()) {
                try {
                    // Validate ảnh trước khi upload
                    ImageService.ImageValidationResult validation = imageService.validateImage(avatarFile);
                    if (!validation.isValid()) {
                        redirectAttributes.addFlashAttribute("error", validation.getMessage());
                        return "redirect:/profile/edit";
                    }
                    
                    // Xóa ảnh cũ nếu có
                    if (currentUser.getImage() != null && !currentUser.getImage().isEmpty()) {
                        imageService.deleteAvatar(currentUser.getImage());
                    }
                    
                    // Upload ảnh mới
                    String fileName = imageService.uploadAvatar(avatarFile, currentUser.getUserId());
                    currentUser.setImage(fileName);
                    
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("error", "Lỗi khi upload ảnh: " + e.getMessage());
                    return "redirect:/profile/edit";
                }
            }
            
            // Lưu vào database
            User savedUser = userService.save(currentUser);
            
            // Cập nhật session
            session.setAttribute("user", savedUser);
            
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
            return "redirect:/profile";
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật thông tin: " + e.getMessage());
            return "redirect:/profile/edit";
        }
    }
    
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/auth/login";
        }
        
        // Kiểm tra mật khẩu hiện tại
        Optional<User> userOpt = userService.authenticate(sessionUser.getUsername(), currentPassword);
        if (!userOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng");
            return "redirect:/profile/edit";
        }
        
        // Kiểm tra mật khẩu mới và xác nhận
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới và xác nhận không khớp");
            return "redirect:/profile/edit";
        }
        
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự");
            return "redirect:/profile/edit";
        }
        
        try {
            User currentUser = userOpt.get();
            currentUser.setPassword(newPassword);
            userService.save(currentUser);
            
            redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
            return "redirect:/profile";
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi đổi mật khẩu");
            return "redirect:/profile/edit";
        }
    }
    
    @PostMapping("/delete-avatar")
    public String deleteAvatar(HttpSession session, RedirectAttributes redirectAttributes) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/auth/login";
        }
        
        try {
            Optional<User> currentUserOpt = userService.findById(sessionUser.getUserId());
            if (currentUserOpt.isPresent()) {
                User currentUser = currentUserOpt.get();
                
                // Xóa file ảnh cũ nếu có
                if (currentUser.getImage() != null && !currentUser.getImage().isEmpty()) {
                    imageService.deleteAvatar(currentUser.getImage());
                }
                
                currentUser.setImage(null);
                User savedUser = userService.save(currentUser);
                session.setAttribute("user", savedUser);
                
                redirectAttributes.addFlashAttribute("success", "Đã xóa ảnh đại diện");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa ảnh đại diện");
        }
        
        return "redirect:/profile";
    }
    
}
