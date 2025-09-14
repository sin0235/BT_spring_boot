package vn.iotstar.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "Videos")
public class Video implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private Integer videoId;

    @NotBlank(message = "Tiêu đề video không được để trống")
    @Size(min = 3, max = 200, message = "Tiêu đề video phải từ 3-200 ký tự")
    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Size(max = 1000, message = "Mô tả không quá 1000 ký tự")
    @Column(name = "description", length = 1000)
    private String description;

    @NotBlank(message = "URL video không được để trống")
    @Column(name = "video_url", length = 500, nullable = false)
    private String videoUrl;

    @Column(name = "thumbnail", length = 500)
    private String thumbnail;

    @Column(name = "video_filename", length = 255)
    private String videoFilename;

    @Column(name = "thumbnail_filename", length = 255)
    private String thumbnailFilename;

    @Min(value = 0, message = "Thời lượng phải là số dương")
    @Column(name = "duration")
    private Integer duration; // in seconds

    @Min(value = 0, message = "Lượt xem phải là số dương")
    @Column(name = "views", nullable = false)
    private Long views = 0L;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    @NotNull(message = "Category ID không được để trống")
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @NotNull(message = "User ID không được để trống")
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many videos belong to one category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "cate_id", insertable = false, updatable = false)
    private Category category;

    // Many videos belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    // Constructors
    public Video() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Video(String title, String videoUrl, Integer categoryId, Integer userId) {
        this();
        this.title = title;
        this.videoUrl = videoUrl;
        this.categoryId = categoryId;
        this.userId = userId;
    }

    public Video(String title, String description, String videoUrl, String thumbnail, 
                 Integer categoryId, Integer userId) {
        this();
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnail = thumbnail;
        this.categoryId = categoryId;
        this.userId = userId;
    }

    // Getters and Setters
    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVideoFilename() {
        return videoFilename;
    }

    public void setVideoFilename(String videoFilename) {
        this.videoFilename = videoFilename;
    }

    public String getThumbnailFilename() {
        return thumbnailFilename;
    }

    public void setThumbnailFilename(String thumbnailFilename) {
        this.thumbnailFilename = thumbnailFilename;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Boolean getActive() {
        return isActive;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public Boolean isPublic() {
        return isPublic;
    }

    public void setPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Helper methods
    public String getFormattedDuration() {
        if (duration == null || duration <= 0) return "Unknown";
        int hours = duration / 3600;
        int minutes = (duration % 3600) / 60;
        int seconds = duration % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    public String getFormattedViews() {
        if (views == null) return "0";
        if (views < 1000) return views.toString();
        if (views < 1000000) return String.format("%.1fK", views / 1000.0);
        return String.format("%.1fM", views / 1000000.0);
    }

    public String getStatusBadge() {
        if (isActive == null || !isActive) return "<span class=\"badge bg-danger\">Inactive</span>";
        if (isPublic == null || !isPublic) return "<span class=\"badge bg-warning\">Private</span>";
        return "<span class=\"badge bg-success\">Public</span>";
    }

    public String getThumbnailUrl() {
        return thumbnailFilename != null ? "/uploads/" + thumbnailFilename : "/images/default-video-thumbnail.jpg";
    }

    public String getVideoFullUrl() {
        return videoFilename != null ? "/uploads/" + videoFilename : videoUrl;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Video{" +
                "videoId=" + videoId +
                ", title='" + title + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", categoryId=" + categoryId +
                ", userId=" + userId +
                ", views=" + views +
                ", isActive=" + isActive +
                ", isPublic=" + isPublic +
                '}';
    }
}
