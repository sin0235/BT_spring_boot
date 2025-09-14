package vn.iotstar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.entity.User;
import vn.iotstar.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class UserManagementController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String listUsers(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "") String search,
                           @RequestParam(defaultValue = "0") int roleFilter,
                           HttpSession session, Model model) {
        
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) { // Admin only
            return "redirect:/auth/login";
        }
        
        List<User> users;
        if (!search.isEmpty()) {
            users = userService.searchUsers(search);
        } else if (roleFilter > 0) {
            users = userService.findByRole(roleFilter);
        } else {
            users = userService.findAll();
        }
        
        model.addAttribute("users", users);
        model.addAttribute("search", search);
        model.addAttribute("roleFilter", roleFilter);
        model.addAttribute("currentUser", user);
        
        return "admin/users/list";
    }
    
    @GetMapping("/new")
    public String newUserForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("user", new User());
        model.addAttribute("isEdit", false);
        return "admin/users/form";
    }
    
    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Integer id, HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null || sessionUser.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        Optional<User> userOpt = userService.findById(id);
        if (!userOpt.isPresent()) {
            return "redirect:/admin/users?error=notfound";
        }
        
        model.addAttribute("user", userOpt.get());
        model.addAttribute("isEdit", true);
        return "admin/users/form";
    }
    
    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute User user,
                          BindingResult bindingResult,
                          @RequestParam(required = false) boolean isEdit,
                          HttpSession session,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null || sessionUser.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("isEdit", isEdit);
            return "admin/users/form";
        }
        
        try {
            if (isEdit) {
                // Cập nhật user
                Optional<User> existingUserOpt = userService.findById(user.getUserId());
                if (!existingUserOpt.isPresent()) {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng");
                    return "redirect:/admin/users";
                }
                
                User existingUser = existingUserOpt.get();
                existingUser.setFullName(user.getFullName());
                existingUser.setEmail(user.getEmail());
                existingUser.setPhone(user.getPhone());
                existingUser.setRoleId(user.getRoleId());
                existingUser.setActive(user.isActive());
                existingUser.setUpdatedAt(LocalDateTime.now());
                
                // Chỉ update password nếu có nhập mới
                if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                    existingUser.setPassword(user.getPassword());
                }
                
                userService.save(existingUser);
                redirectAttributes.addFlashAttribute("success", "Cập nhật người dùng thành công!");
                
            } else {
                // Tạo user mới
                if (userService.existsByUsername(user.getUsername())) {
                    redirectAttributes.addFlashAttribute("error", "Tên đăng nhập đã tồn tại");
                    return "redirect:/admin/users/new";
                }
                
                if (userService.existsByEmail(user.getEmail())) {
                    redirectAttributes.addFlashAttribute("error", "Email đã tồn tại");
                    return "redirect:/admin/users/new";
                }
                
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                user.setActive(true);
                
                userService.save(user);
                redirectAttributes.addFlashAttribute("success", "Tạo người dùng thành công!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return isEdit ? "redirect:/admin/users/edit/" + user.getUserId() : "redirect:/admin/users/new";
        }
        
        return "redirect:/admin/users";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Integer id,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null || sessionUser.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        // Không cho phép xóa chính mình
        if (sessionUser.getUserId() == id) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa tài khoản của chính mình");
            return "redirect:/admin/users";
        }
        
        try {
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isPresent()) {
                userService.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "Xóa người dùng thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa người dùng");
        }
        
        return "redirect:/admin/users";
    }
    
    @PostMapping("/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable Integer id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null || sessionUser.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        // Không cho phép vô hiệu hóa chính mình
        if (sessionUser.getUserId() == id) {
            redirectAttributes.addFlashAttribute("error", "Không thể thay đổi trạng thái tài khoản của chính mình");
            return "redirect:/admin/users";
        }
        
        try {
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setActive(!user.isActive());
                user.setUpdatedAt(LocalDateTime.now());
                userService.save(user);
                
                String status = user.isActive() ? "kích hoạt" : "vô hiệu hóa";
                redirectAttributes.addFlashAttribute("success", "Đã " + status + " tài khoản thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thay đổi trạng thái");
        }
        
        return "redirect:/admin/users";
    }
    
    @GetMapping("/view/{id}")
    public String viewUser(@PathVariable Integer id, HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null || sessionUser.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        Optional<User> userOpt = userService.findById(id);
        if (!userOpt.isPresent()) {
            return "redirect:/admin/users?error=notfound";
        }
        
        model.addAttribute("user", userOpt.get());
        return "admin/users/view";
    }
}
