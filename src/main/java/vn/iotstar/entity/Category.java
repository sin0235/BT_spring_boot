package vn.iotstar.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "Category")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cate_id")
    private Integer cateid;

    @Column(name = "cate_name")
    private String catename;

    @Column(name = "icon_path")
    private String iconPath;
    
    @Column(name = "icon_filename")
    private String iconFilename;
    
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Many categories belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    // Constructors
    public Category() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Category(String catename, Integer userId) {
        this();
        this.catename = catename;
        this.userId = userId;
    }
    
    public Category(String catename, String iconPath, String iconFilename, Integer userId) {
        this();
        this.catename = catename;
        this.iconPath = iconPath;
        this.iconFilename = iconFilename;
        this.userId = userId;
    }

    // Getters and setters
    public Integer getCateid() { 
        return cateid; 
    }
    
    public void setCateid(Integer cateid) { 
        this.cateid = cateid; 
    }

    public String getCatename() { 
        return catename; 
    }
    
    public void setCatename(String catename) { 
        this.catename = catename; 
    }

    public String getIconPath() { 
        return iconPath; 
    }
    
    public void setIconPath(String iconPath) { 
        this.iconPath = iconPath; 
    }
    
    public String getIconFilename() { 
        return iconFilename; 
    }
    
    public void setIconFilename(String iconFilename) { 
        this.iconFilename = iconFilename; 
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    // Helper method to get full icon URL
    public String getIconUrl() {
        return iconFilename != null ? "/category-icons/" + iconFilename : null;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Category{" +
                "cateid=" + cateid +
                ", catename='" + catename + '\'' +
                ", iconPath='" + iconPath + '\'' +
                ", iconFilename='" + iconFilename + '\'' +
                ", userId=" + userId +
                '}';
    }
}