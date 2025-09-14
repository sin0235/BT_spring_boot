package vn.iotstar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.User;
import vn.iotstar.entity.Video;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.UserService;
import vn.iotstar.service.VideoService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private VideoService videoService;
    
    // Dashboard
    @GetMapping("/home")
    public String adminHome(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        // Add statistics for admin dashboard
        long totalUsers = userService.getTotalUsers();
        long totalCategories = categoryService.countAllCategories();
        long totalVideos = videoService.getTotalVideos();
        long activeUsers = userService.countActiveUsers();
        long publicVideos = videoService.countPublicVideos();
        
        // Add all categories for admin to view
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalVideos", totalVideos);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("publicVideos", publicVideos);
        model.addAttribute("user", user);
        
        return "admin/home";
    }
    
    // ===================== USER MANAGEMENT =====================
    
    @GetMapping("/users")
    public String listUsers(HttpSession session, Model model,
                           @RequestParam(value = "search", required = false) String search,
                           @RequestParam(value = "role", required = false) Integer roleFilter,
                           @RequestParam(value = "status", required = false) Boolean statusFilter) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("users", userService.adminSearchUsers(search, roleFilter, statusFilter));
        model.addAttribute("search", search);
        model.addAttribute("roleFilter", roleFilter);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("user", user);
        
        return "admin/users/list";
    }
    
    @GetMapping("/users/add")
    public String addUserForm(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null || sessionUser.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("user", sessionUser);
        model.addAttribute("newUser", new User());
        model.addAttribute("isEdit", false);
        
        return "admin/users/form";
    }
    
    @PostMapping("/users/add")
    public String addUser(HttpSession session, @Valid @ModelAttribute("newUser") User newUser,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null || sessionUser.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        if (result.hasErrors()) {
            model.addAttribute("user", sessionUser);
            model.addAttribute("isEdit", false);
            return "admin/users/form";
        }
        
        try {
            userService.createUser(newUser);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", sessionUser);
            model.addAttribute("isEdit", false);
            return "admin/users/form";
        }
        
        return "redirect:/admin/users";
    }
    
    @GetMapping("/users/edit/{id}")
    public String editUserForm(HttpSession session, @PathVariable Integer id, Model model) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null || sessionUser.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        User editUser = userService.findById(id).orElse(null);
        if (editUser == null) {
            return "redirect:/admin/users";
        }
        
        model.addAttribute("user", sessionUser);
        model.addAttribute("editUser", editUser);
        model.addAttribute("isEdit", true);
        
        return "admin/users/form";
    }
    
    @PostMapping("/users/edit/{id}")
    public String editUser(HttpSession session, @PathVariable Integer id,
                          @Valid @ModelAttribute("editUser") User editUser,
                          BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null || sessionUser.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        if (result.hasErrors()) {
            model.addAttribute("user", sessionUser);
            model.addAttribute("isEdit", true);
            return "admin/users/form";
        }
        
        try {
            editUser.setUserId(id);
            userService.updateUser(editUser);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", sessionUser);
            model.addAttribute("isEdit", true);
            return "admin/users/form";
        }
        
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/toggle/{id}")
    public String toggleUserStatus(HttpSession session, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        userService.toggleUserStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "User status updated successfully!");
        
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/delete/{id}")
    public String deleteUser(HttpSession session, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        
        return "redirect:/admin/users";
    }
    
    // ===================== CATEGORY MANAGEMENT =====================
    
    @GetMapping("/categories")
    public String listCategories(HttpSession session, Model model,
                                @RequestParam(value = "search", required = false) String search,
                                @RequestParam(value = "userId", required = false) Integer userFilter) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("categories", categoryService.adminSearchCategories(search, userFilter));
        model.addAttribute("users", userService.findAll());
        model.addAttribute("search", search);
        model.addAttribute("userFilter", userFilter);
        model.addAttribute("user", user);
        
        return "admin/categories/list";
    }
    
    @GetMapping("/categories/add")
    public String addCategoryForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("category", new Category());
        model.addAttribute("isEdit", false);
        
        return "admin/categories/form";
    }
    
    @PostMapping("/categories/add")
    public String addCategory(HttpSession session, @Valid @ModelAttribute Category category,
                             BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("users", userService.findAll());
            model.addAttribute("isEdit", false);
            return "admin/categories/form";
        }
        
        try {
            categoryService.createCategory(category);
            redirectAttributes.addFlashAttribute("successMessage", "Category created successfully!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("users", userService.findAll());
            model.addAttribute("isEdit", false);
            return "admin/categories/form";
        }
        
        return "redirect:/admin/categories";
    }
    
    @GetMapping("/categories/edit/{id}")
    public String editCategoryForm(HttpSession session, @PathVariable Integer id, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        Category category = categoryService.findById(id).orElse(null);
        if (category == null) {
            return "redirect:/admin/categories";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("category", category);
        model.addAttribute("isEdit", true);
        
        return "admin/categories/form";
    }
    
    @PostMapping("/categories/edit/{id}")
    public String editCategory(HttpSession session, @PathVariable Integer id,
                              @Valid @ModelAttribute Category category,
                              BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("users", userService.findAll());
            model.addAttribute("isEdit", true);
            return "admin/categories/form";
        }
        
        try {
            category.setCateid(id);
            categoryService.updateCategory(category);
            redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("users", userService.findAll());
            model.addAttribute("isEdit", true);
            return "admin/categories/form";
        }
        
        return "redirect:/admin/categories";
    }
    
    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(HttpSession session, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        categoryService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully!");
        
        return "redirect:/admin/categories";
    }
    
    // ===================== VIDEO MANAGEMENT =====================
    
    @GetMapping("/videos")
    public String listVideos(HttpSession session, Model model,
                            @RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "userId", required = false) Integer userFilter,
                            @RequestParam(value = "categoryId", required = false) Integer categoryFilter,
                            @RequestParam(value = "isActive", required = false) Boolean statusFilter) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("videos", videoService.searchWithFilters(userFilter, categoryFilter, statusFilter, null, search));
        model.addAttribute("users", userService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("search", search);
        model.addAttribute("userFilter", userFilter);
        model.addAttribute("categoryFilter", categoryFilter);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("user", user);
        
        return "admin/videos/list";
    }
    
    @GetMapping("/videos/add")
    public String addVideoForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("video", new Video());
        model.addAttribute("isEdit", false);
        
        return "admin/videos/form";
    }
    
    @PostMapping("/videos/add")
    public String addVideo(HttpSession session, @Valid @ModelAttribute Video video,
                          BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("users", userService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("isEdit", false);
            return "admin/videos/form";
        }
        
        videoService.createVideo(video);
        redirectAttributes.addFlashAttribute("successMessage", "Video created successfully!");
        
        return "redirect:/admin/videos";
    }
    
    @GetMapping("/videos/edit/{id}")
    public String editVideoForm(HttpSession session, @PathVariable Integer id, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        Video video = videoService.findById(id).orElse(null);
        if (video == null) {
            return "redirect:/admin/videos";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("video", video);
        model.addAttribute("isEdit", true);
        
        return "admin/videos/form";
    }
    
    @PostMapping("/videos/edit/{id}")
    public String editVideo(HttpSession session, @PathVariable Integer id,
                           @Valid @ModelAttribute Video video,
                           BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("users", userService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("isEdit", true);
            return "admin/videos/form";
        }
        
        video.setVideoId(id);
        videoService.updateVideo(video);
        redirectAttributes.addFlashAttribute("successMessage", "Video updated successfully!");
        
        return "redirect:/admin/videos";
    }
    
    @PostMapping("/videos/toggle/{id}")
    public String toggleVideoStatus(HttpSession session, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        videoService.togglePublicStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Video status updated successfully!");
        
        return "redirect:/admin/videos";
    }
    
    @PostMapping("/videos/delete/{id}")
    public String deleteVideo(HttpSession session, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleId() != 3) {
            return "redirect:/auth/login";
        }
        
        videoService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Video deleted successfully!");
        
        return "redirect:/admin/videos";
    }
}