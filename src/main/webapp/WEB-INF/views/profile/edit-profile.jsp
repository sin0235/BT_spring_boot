<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Edit Profile - ${user.fullName}" />
<jsp:include page="/WEB-INF/includes/header.jsp" />

<style>
        .edit-profile-container {
            max-width: 800px;
            margin: 0 auto;
        }
        .profile-form {
            background: white;
            border-radius: 15px;
            padding: 2rem;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
        .current-avatar {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            object-fit: cover;
            border: 4px solid #dee2e6;
        }
        .file-upload-container {
            position: relative;
            display: inline-block;
        }
        .file-upload-input {
            position: absolute;
            opacity: 0;
            width: 100%;
            height: 100%;
            cursor: pointer;
        }
        .file-upload-label {
            display: inline-block;
            padding: 0.5rem 1rem;
            background-color: #6c757d;
            color: white;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        .file-upload-label:hover {
            background-color: #5a6268;
        }
        .preview-container {
            margin-top: 1rem;
        }
        .preview-image {
            max-width: 200px;
            max-height: 200px;
            border-radius: 10px;
            border: 2px solid #dee2e6;
        }
        .form-section {
            margin-bottom: 2rem;
            padding-bottom: 2rem;
            border-bottom: 1px solid #dee2e6;
        }
        .form-section:last-child {
            border-bottom: none;
            margin-bottom: 0;
        }
        .required {
            color: #dc3545;
        }
    </style>

    <div class="container py-4">
        <div class="edit-profile-container">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2><i class="fas fa-edit me-2"></i>Edit Profile</h2>
                <a href="${pageContext.request.contextPath}/profile" class="btn btn-outline-secondary">
                    <i class="fas fa-arrow-left me-2"></i>Back to Profile
                </a>
            </div>

            <div class="profile-form">
                <form action="${pageContext.request.contextPath}/profile/update" method="post" 
                      enctype="multipart/form-data" id="profileForm">
                    
                    <!-- Profile Picture Section -->
                    <div class="form-section">
                        <h5 class="mb-3"><i class="fas fa-camera me-2"></i>Profile Picture</h5>
                        
                        <div class="row align-items-center">
                            <div class="col-md-4 text-center">
                                <div class="mb-3">
                                    <c:choose>
                                        <c:when test="${not empty user.image}">
                                            <img src="${pageContext.request.contextPath}/uploads/${user.image}" 
                                                 alt="Current Profile Picture" class="current-avatar" id="currentAvatar">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="current-avatar d-flex align-items-center justify-content-center" 
                                                 style="background-color: #6c757d; color: white;" id="currentAvatar">
                                                <i class="fas fa-user fa-3x"></i>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <small class="text-muted">Current Picture</small>
                            </div>
                            
                            <div class="col-md-8">
                                <div class="mb-3">
                                    <label for="image" class="form-label">Upload New Picture</label>
                                    <div class="file-upload-container">
                                        <input type="file" class="file-upload-input" id="image" name="image" 
                                               accept="image/*" onchange="previewImage(this)">
                                        <label for="image" class="file-upload-label">
                                            <i class="fas fa-upload me-2"></i>Choose Image
                                        </label>
                                    </div>
                                    <div class="form-text">
                                        Supported formats: JPG, JPEG, PNG, GIF, BMP. Maximum size: 5MB.
                                    </div>
                                </div>
                                
                                <div class="preview-container" id="previewContainer" style="display: none;">
                                    <label class="form-label">Preview</label>
                                    <div>
                                        <img id="imagePreview" class="preview-image" alt="Preview">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Personal Information Section -->
                    <div class="form-section">
                        <h5 class="mb-3"><i class="fas fa-user me-2"></i>Personal Information</h5>
                        
                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="username" class="form-label">Username</label>
                                    <input type="text" class="form-control" id="username" 
                                           value="${user.username}" readonly disabled>
                                    <div class="form-text">Username cannot be changed</div>
                                </div>
                            </div>
                            
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="email" class="form-label">Email</label>
                                    <input type="email" class="form-control" id="email" 
                                           value="${user.email}" readonly disabled>
                                    <div class="form-text">Email cannot be changed</div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="fullName" class="form-label">
                                        Full Name <span class="required">*</span>
                                    </label>
                                    <input type="text" class="form-control" id="fullName" name="fullName" 
                                           value="${user.fullName}" required maxlength="100">
                                    <div class="invalid-feedback">
                                        Please provide a valid full name.
                                    </div>
                                </div>
                            </div>
                            
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="phone" class="form-label">Phone Number</label>
                                    <input type="tel" class="form-control" id="phone" name="phone" 
                                           value="${user.phone}" maxlength="20" 
                                           pattern="[0-9+\-\s()]+" placeholder="e.g., +84 123 456 789">
                                    <div class="form-text">Optional. Include country code if international.</div>
                                    <div class="invalid-feedback">
                                        Please provide a valid phone number.
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Form Actions -->
                    <div class="d-flex gap-3">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save me-2"></i>Save Changes
                        </button>
                        <a href="${pageContext.request.contextPath}/profile" class="btn btn-secondary">
                            <i class="fas fa-times me-2"></i>Cancel
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        // Image preview functionality
        function previewImage(input) {
            const previewContainer = document.getElementById('previewContainer');
            const imagePreview = document.getElementById('imagePreview');
            
            if (input.files && input.files[0]) {
                const file = input.files[0];
                
                // Validate file size (5MB = 5 * 1024 * 1024 bytes)
                if (file.size > 5 * 1024 * 1024) {
                    alert('File size exceeds 5MB limit. Please choose a smaller image.');
                    input.value = '';
                    previewContainer.style.display = 'none';
                    return;
                }
                
                // Validate file type
                const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/bmp'];
                if (!allowedTypes.includes(file.type)) {
                    alert('Invalid file type. Please choose an image file (JPG, PNG, GIF, BMP).');
                    input.value = '';
                    previewContainer.style.display = 'none';
                    return;
                }
                
                const reader = new FileReader();
                reader.onload = function(e) {
                    imagePreview.src = e.target.result;
                    previewContainer.style.display = 'block';
                };
                reader.readAsDataURL(file);
            } else {
                previewContainer.style.display = 'none';
            }
        }

        // Form validation
        (function() {
            'use strict';
            const form = document.getElementById('profileForm');
            
            form.addEventListener('submit', function(event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        })();

        // Phone number formatting
        document.getElementById('phone').addEventListener('input', function(e) {
            let value = e.target.value.replace(/[^\d+\-\s()]/g, '');
            e.target.value = value;
        });
    </script>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
