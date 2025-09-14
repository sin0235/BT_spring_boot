package vn.iotstar.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/uploads")
public class ImageController {
    
    private final String UPLOAD_DIR = "uploads/";
    
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        
        // Validate filename to prevent directory traversal
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            // Build path to uploaded file
            String uploadPath = System.getProperty("user.dir") + File.separator + UPLOAD_DIR;
            Path imagePath = Paths.get(uploadPath + filename);
            
            File imageFile = imagePath.toFile();
            if (!imageFile.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(imageFile);
            
            // Determine content type based on file extension
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM; // Default
            String lowerFileName = filename.toLowerCase();
            
            if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) {
                mediaType = MediaType.IMAGE_JPEG;
            } else if (lowerFileName.endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            } else if (lowerFileName.endsWith(".gif")) {
                mediaType = MediaType.IMAGE_GIF;
            } else if (lowerFileName.endsWith(".webp")) {
                mediaType = MediaType.parseMediaType("image/webp");
            } else if (lowerFileName.endsWith(".svg")) {
                mediaType = MediaType.parseMediaType("image/svg+xml");
            }
            
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
