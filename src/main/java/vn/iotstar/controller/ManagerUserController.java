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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager/users")
public class ManagerUserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String listTeamUsers(@RequestParam(defaultValue = "") String search,
                               HttpSession session, Model model) {
        
        User manager = (User) session.getAttribute("user");
        if (manager == null || manager.getRoleId() != 2) { // Manager only
            return "redirect:/auth/login";
        }
        
        List<User> allUsers;
        if (!search.isEmpty()) {
            allUsers = userService.searchUsers(search);
        } else {
            allUsers = userService.findAll();
        }
        
        // Manager chỉ được xem users (roleId = 1) và managers khác
        List<User> teamUsers = allUsers.stream()
                .filter(user -> user.getRoleId() == 1 || (user.getRoleId() == 2 && user.getUserId() != manager.getUserId()))
                .collect(Collectors.toList());
        
        model.addAttribute("users", teamUsers);
        model.addAttribute("search", search);
        model.addAttribute("currentUser", manager);
        
        return "manager/users/list";
    }
    
    @GetMapping("/new")
    public String newUserForm(HttpSession session, Model model) {
        User manager = (User) session.getAttribute("user");
        if (manager == null || manager.getRoleId() != 2) {
            return "redirect:/auth/login";
        }
        
        User newUser = new User();
        newUser.setRoleId(1); // Mặc định tạo user với role User
        model.addAttribute("user", newUser);
        model.addAttribute("isEdit", false);
        return "manager/users/form";
    }
    
    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Integer id, HttpSession session, Model model) {
        User manager = (User) session.getAttribute("user");
        if (manager == null || manager.getRoleId() != 2) {
            return "redirect:/auth/login";
        }
        
        Optional<User> userOpt = userService.findById(id);
        if (!userOpt.isPresent()) {
            return "redirect:/manager/users?error=notfound";
        }
        
        User user = userOpt.get();
        // Manager chỉ được chỉnh sửa users (roleId = 1) hoặc managers khác (không phải admin)
        if (user.getRoleId() == 3) {
            return "redirect:/manager/users?error=unauthorized";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("isEdit", true);
        return "manager/users/form";
    }
    
    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user,
                          @RequestParam(required = false) boolean isEdit,
                          HttpSession session,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        
        User manager = (User) session.getAttribute("user");
        if (manager == null || manager.getRoleId() != 2) {
            return "redirect:/auth/login";
        }
        
        try {
            if (isEdit) {
                // Cập nhật user
                Optional<User> existingUserOpt = userService.findById(user.getUserId());
                if (!existingUserOpt.isPresent()) {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng");
                    return "redirect:/manager/users";
                }
                
                User existingUser = existingUserOpt.get();
                
                // Kiểm tra quyền: Manager không được sửa Admin
                if (existingUser.getRoleId() == 3) {
                    redirectAttributes.addFlashAttribute("error", "Không có quyền chỉnh sửa Admin");
                    return "redirect:/manager/users";
                }
                
                existingUser.setFullName(user.getFullName());
                existingUser.setEmail(user.getEmail());
                existingUser.setPhone(user.getPhone());
                existingUser.setActive(user.isActive());
                existingUser.setUpdatedAt(LocalDateTime.now());
                
                // Manager chỉ được set role User (1) hoặc Manager (2), không được set Admin (3)
                if (user.getRoleId() == 1 || user.getRoleId() == 2) {
                    existingUser.setRoleId(user.getRoleId());
                }
                
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
                    return "redirect:/manager/users/new";
                }
                
                if (userService.existsByEmail(user.getEmail())) {
                    redirectAttributes.addFlashAttribute("error", "Email đã tồn tại");
                    return "redirect:/manager/users/new";
                }
                
                // Manager chỉ được tạo User (1) hoặc Manager (2)
                if (user.getRoleId() != 1 && user.getRoleId() != 2) {
                    user.setRoleId(1); // Mặc định là User
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
            return isEdit ? "redirect:/manager/users/edit/" + user.getUserId() : "redirect:/manager/users/new";
        }
        
        return "redirect:/manager/users";
    }
    
    @PostMapping("/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable Integer id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        
        User manager = (User) session.getAttribute("user");
        if (manager == null || manager.getRoleId() != 2) {
            return "redirect:/auth/login";
        }
        
        try {
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Manager không được thay đổi trạng thái Admin
                if (user.getRoleId() == 3) {
                    redirectAttributes.addFlashAttribute("error", "Không có quyền thay đổi trạng thái Admin");
                    return "redirect:/manager/users";
                }
                
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
        
        return "redirect:/manager/users";
    }
    
    @GetMapping("/view/{id}")
    public String viewUser(@PathVariable Integer id, HttpSession session, Model model) {
        User manager = (User) session.getAttribute("user");
        if (manager == null || manager.getRoleId() != 2) {
            return "redirect:/auth/login";
        }
        
        Optional<User> userOpt = userService.findById(id);
        if (!userOpt.isPresent()) {
            return "redirect:/manager/users?error=notfound";
        }
        
        User user = userOpt.get();
        // Manager không được xem thông tin Admin
        if (user.getRoleId() == 3) {
            return "redirect:/manager/users?error=unauthorized";
        }
        
        model.addAttribute("user", user);
        return "manager/users/view";
    }
    
    @GetMapping("/statistics")
    public String teamStatistics(HttpSession session, Model model) {
        User manager = (User) session.getAttribute("user");
        if (manager == null || manager.getRoleId() != 2) {
            return "redirect:/auth/login";
        }
        
        List<User> allUsers = userService.findAll();
        
        // Thống kê users dưới quyền manager (không bao gồm Admin)
        List<User> teamUsers = allUsers.stream()
                .filter(user -> user.getRoleId() == 1 || user.getRoleId() == 2)
                .collect(Collectors.toList());
        
        long totalTeamUsers = teamUsers.size();
        long activeUsers = teamUsers.stream().mapToLong(user -> user.isActive() ? 1 : 0).sum();
        long inactiveUsers = totalTeamUsers - activeUsers;
        long regularUsers = teamUsers.stream().mapToLong(user -> user.getRoleId() == 1 ? 1 : 0).sum();
        long managers = teamUsers.stream().mapToLong(user -> user.getRoleId() == 2 ? 1 : 0).sum();
        
        model.addAttribute("totalTeamUsers", totalTeamUsers);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("inactiveUsers", inactiveUsers);
        model.addAttribute("regularUsers", regularUsers);
        model.addAttribute("managers", managers);
        model.addAttribute("recentUsers", teamUsers.stream()
                .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList()));
        
        return "manager/statistics";
    }
}
