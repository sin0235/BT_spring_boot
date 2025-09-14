package vn.iotstar.utils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileUtils {
    
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB for profile images
    private static final String UPLOAD_DIRECTORY = "uploads";
    
    public static String createUploadDirectory(String directoryPath) throws IOException {
        Path uploadPath = Paths.get(directoryPath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        return uploadPath.toString();
    }
    
    public static String generateUniqueFileName(String originalName) {
        String extension = getFileExtension(originalName);
        return UUID.randomUUID().toString() + extension;
    }
    
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }
    
    public static boolean isValidImageFile(String fileName) {
        if (fileName == null) return false;
        
        String extension = getFileExtension(fileName).toLowerCase();
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isValidFileSize(long fileSize) {
        return fileSize > 0 && fileSize <= MAX_FILE_SIZE;
    }
    
    public static void saveFile(byte[] fileContent, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.write(path, fileContent);
    }
    
    public static void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Log error but don't throw exception
            System.err.println("Failed to delete file: " + filePath + ", Error: " + e.getMessage());
        }
    }
    
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
    
    /**
     * Save uploaded file and return the file name
     */
    public static String saveUploadedFile(Part filePart, ServletContext context) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }
        
        // Get original filename
        String originalFileName = getFileName(filePart);
        if (originalFileName == null || originalFileName.isEmpty()) {
            return null;
        }
        
        // Validate file
        if (!isValidImageFile(originalFileName)) {
            throw new IOException("Invalid file type. Only image files are allowed.");
        }
        
        if (!isValidFileSize(filePart.getSize())) {
            throw new IOException("File size exceeds the maximum limit of " + formatFileSize(MAX_FILE_SIZE));
        }
        
        // Generate unique file name
        String fileName = generateUniqueFileName(originalFileName);
        
        // Create upload directory
        String uploadPath = context.getRealPath("/") + UPLOAD_DIRECTORY;
        createUploadDirectory(uploadPath);
        
        // Save file
        String filePath = uploadPath + File.separator + fileName;
        try (InputStream inputStream = filePart.getInputStream()) {
            Files.copy(inputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        }
        
        return fileName;
    }
    
    /**
     * Delete uploaded file from uploads directory
     */
    public static void deleteUploadedFile(String fileName, ServletContext context) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        
        String uploadPath = context.getRealPath("/") + UPLOAD_DIRECTORY;
        String filePath = uploadPath + File.separator + fileName;
        deleteFile(filePath);
    }
    
    /**
     * Extract filename from multipart Part
     */
    private static String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return null;
        }
        
        for (String content : contentDisposition.split(";")) {
            if (content.trim().startsWith("filename")) {
                String fileName = content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
                return fileName.isEmpty() ? null : fileName;
            }
        }
        return null;
    }
}