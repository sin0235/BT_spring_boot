package vn.iotstar.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

@Service
public class ImageService {
    
    private final String UPLOAD_DIR = "uploads/avatars/";
    private final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private final int MAX_WIDTH = 500;
    private final int MAX_HEIGHT = 500;
    
    public String uploadAvatar(MultipartFile file, Integer userId) throws IOException {
        // Kiểm tra file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }
        
        // Kiểm tra kích thước
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Kích thước file không được vượt quá 5MB");
        }
        
        // Kiểm tra định dạng
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidImageFormat(originalFilename)) {
            throw new IllegalArgumentException("Chỉ hỗ trợ các định dạng: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
        
        // Tạo tên file duy nhất
        String fileExtension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String fileName = String.format("avatar_%d_%s_%s.%s", userId, timestamp, uniqueId, fileExtension);
        
        // Tạo thư mục nếu chưa tồn tại
        String uploadPath = System.getProperty("user.dir") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        // Đọc và resize ảnh
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IllegalArgumentException("File không phải là ảnh hợp lệ");
        }
        
        BufferedImage resizedImage = resizeImage(originalImage, MAX_WIDTH, MAX_HEIGHT);
        
        // Lưu ảnh đã resize
        Path filePath = Paths.get(uploadPath + fileName);
        ImageIO.write(resizedImage, fileExtension, filePath.toFile());
        
        return fileName;
    }
    
    public void deleteAvatar(String imagePath) {
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                String fullPath = System.getProperty("user.dir") + File.separator + UPLOAD_DIR + imagePath;
                File file = new File(fullPath);
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            // Log error nhưng không throw exception để không ảnh hưởng đến flow chính
            System.err.println("Lỗi khi xóa ảnh: " + e.getMessage());
        }
    }
    
    public String getAvatarUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        return "/uploads/avatars/" + imagePath;
    }
    
    public String getDefaultAvatarClass(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "bg-secondary";
        }
        
        // Tạo màu dựa trên tên để có tính nhất quán
        int hash = fullName.hashCode();
        String[] colors = {"bg-primary", "bg-success", "bg-info", "bg-warning", "bg-danger", "bg-dark"};
        return colors[Math.abs(hash) % colors.length];
    }
    
    public String getInitials(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "U";
        }
        
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
        } else {
            return fullName.substring(0, Math.min(2, fullName.length())).toUpperCase();
        }
    }
    
    public boolean isValidImageFormat(String fileName) {
        String extension = getFileExtension(fileName);
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }
    
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }
    
    private BufferedImage resizeImage(BufferedImage original, int maxWidth, int maxHeight) {
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();
        
        // Tính toán kích thước mới giữ tỷ lệ
        double aspectRatio = (double) originalWidth / originalHeight;
        int newWidth = maxWidth;
        int newHeight = maxHeight;
        
        if (originalWidth > maxWidth || originalHeight > maxHeight) {
            if (aspectRatio > 1) {
                newHeight = (int) (maxWidth / aspectRatio);
            } else {
                newWidth = (int) (maxHeight * aspectRatio);
            }
        } else {
            newWidth = originalWidth;
            newHeight = originalHeight;
        }
        
        // Tạo ảnh mới với chất lượng cao
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        
        return resized;
    }
    
    public ImageValidationResult validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            return new ImageValidationResult(false, "Vui lòng chọn file ảnh");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            return new ImageValidationResult(false, "Kích thước file không được vượt quá 5MB");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidImageFormat(originalFilename)) {
            return new ImageValidationResult(false, "Chỉ hỗ trợ các định dạng: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
        
        return new ImageValidationResult(true, "File hợp lệ");
    }
    
    public static class ImageValidationResult {
        private final boolean valid;
        private final String message;
        
        public ImageValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
    }
}

