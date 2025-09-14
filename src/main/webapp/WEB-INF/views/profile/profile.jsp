<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Profile - ${user.fullName}" />
<jsp:include page="/WEB-INF/includes/header.jsp" />

<style>
        .profile-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 2rem 0;
            margin-bottom: 2rem;
        }
        .profile-avatar {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            object-fit: cover;
            border: 4px solid white;
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        }
        .profile-info {
            background: white;
            border-radius: 15px;
            padding: 2rem;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            margin-bottom: 2rem;
        }
        .info-item {
            display: flex;
            align-items: center;
            padding: 1rem 0;
            border-bottom: 1px solid #eee;
        }
        .info-item:last-child {
            border-bottom: none;
        }
        .info-icon {
            width: 40px;
            text-align: center;
            color: #6c757d;
            margin-right: 1rem;
        }
        .info-label {
            font-weight: 600;
            color: #495057;
            min-width: 120px;
        }
        .info-value {
            color: #212529;
        }
        .role-badge {
            font-size: 0.875rem;
            padding: 0.5rem 1rem;
            border-radius: 20px;
        }
        .role-admin {
            background-color: #dc3545;
            color: white;
        }
        .role-manager {
            background-color: #fd7e14;
            color: white;
        }
        .role-user {
            background-color: #28a745;
            color: white;
        }
    </style>
    <div class="profile-header">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-3 text-center">
                    <c:choose>
                        <c:when test="${not empty user.image}">
                            <img src="${pageContext.request.contextPath}/uploads/${user.image}" 
                                 alt="Profile Picture" class="profile-avatar">
                        </c:when>
                        <c:otherwise>
                            <div class="profile-avatar d-flex align-items-center justify-content-center" 
                                 style="background-color: #6c757d;">
                                <i class="fas fa-user fa-3x"></i>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="col-md-9">
                    <h1 class="mb-2">${user.fullName}</h1>
                    <p class="mb-3 opacity-75">@${user.username}</p>
                    <span class="role-badge role-${user.roleName.toLowerCase()}">${user.roleName}</span>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <div class="row">
            <div class="col-md-8">
                <div class="profile-info">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h3 class="mb-0"><i class="fas fa-user me-2"></i>Personal Information</h3>
                        <a href="${pageContext.request.contextPath}/profile/edit" class="btn btn-primary">
                            <i class="fas fa-edit me-2"></i>Edit Profile
                        </a>
                    </div>
                    
                    <div class="info-item">
                        <div class="info-icon">
                            <i class="fas fa-user"></i>
                        </div>
                        <div class="info-label">Full Name:</div>
                        <div class="info-value">${user.fullName}</div>
                    </div>
                    
                    <div class="info-item">
                        <div class="info-icon">
                            <i class="fas fa-at"></i>
                        </div>
                        <div class="info-label">Username:</div>
                        <div class="info-value">${user.username}</div>
                    </div>
                    
                    <div class="info-item">
                        <div class="info-icon">
                            <i class="fas fa-envelope"></i>
                        </div>
                        <div class="info-label">Email:</div>
                        <div class="info-value">${user.email}</div>
                    </div>
                    
                    <div class="info-item">
                        <div class="info-icon">
                            <i class="fas fa-phone"></i>
                        </div>
                        <div class="info-label">Phone:</div>
                        <div class="info-value">
                            <c:choose>
                                <c:when test="${not empty user.phone}">
                                    ${user.phone}
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted">Not provided</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    
                    <div class="info-item">
                        <div class="info-icon">
                            <i class="fas fa-shield-alt"></i>
                        </div>
                        <div class="info-label">Role:</div>
                        <div class="info-value">
                            <span class="role-badge role-${user.roleName.toLowerCase()}">${user.roleName}</span>
                        </div>
                    </div>
                    
                    <div class="info-item">
                        <div class="info-icon">
                            <i class="fas fa-calendar-plus"></i>
                        </div>
                        <div class="info-label">Member Since:</div>
                        <div class="info-value">
                            <c:out value="${user.formattedCreatedAt}" />
                        </div>
                    </div>
                    
                    <c:if test="${user.updatedAt != user.createdAt}">
                        <div class="info-item">
                            <div class="info-icon">
                                <i class="fas fa-edit"></i>
                            </div>
                            <div class="info-label">Last Updated:</div>
                            <div class="info-value">
                                <c:out value="${user.formattedUpdatedAt}" />
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
            
            <div class="col-md-4">
                <div class="profile-info">
                    <h5 class="mb-3"><i class="fas fa-chart-bar me-2"></i>Account Statistics</h5>
                    
                    <div class="text-center">
                        <div class="row">
                            <div class="col-6">
                                <div class="border-end">
                                    <h4 class="text-primary mb-1">${categoriesCount}</h4>
                                    <small class="text-muted">Categories</small>
                                </div>
                            </div>
                            <div class="col-6">
                                <h4 class="text-success mb-1">
                                    <c:out value="${user.statusIcon}" escapeXml="false" />
                                </h4>
                                <small class="text-muted">Status</small>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="profile-info">
                    <h5 class="mb-3"><i class="fas fa-cog me-2"></i>Quick Actions</h5>
                    
                    <div class="d-grid gap-2">
                        <a href="${pageContext.request.contextPath}/profile/edit" class="btn btn-outline-primary">
                            <i class="fas fa-edit me-2"></i>Edit Profile
                        </a>
                        <a href="${pageContext.request.contextPath}/categories" class="btn btn-outline-secondary">
                            <i class="fas fa-list me-2"></i>View Categories
                        </a>
                        <c:if test="${user.admin || user.manager}">
                            <a href="${pageContext.request.contextPath}/admin" class="btn btn-outline-info">
                                <i class="fas fa-cog me-2"></i>Admin Panel
                            </a>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
