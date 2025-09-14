<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="pageTitle" value="User Dashboard - Categories" />
<jsp:include page="/WEB-INF/includes/header.jsp" />

<style>
        .category-icon {
            width: 40px;
            height: 40px;
            object-fit: cover;
            border-radius: 4px;
            border: 1px solid #ddd;
        }
        .no-icon {
            width: 40px;
            height: 40px;
            background-color: #e9ecef;
            border: 1px solid #ddd;
            border-radius: 4px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #6c757d;
            font-size: 10px;
        }
        .owner-info {
            font-size: 12px;
            color: #666;
            font-style: italic;
        }
    </style>

    <div class="content-card">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2><i class="fas fa-list me-2"></i>Categories Dashboard</h2>
            <div>
                <span class="badge bg-primary">Total: ${categories.size()}</span>
            </div>
        </div>
        
        <c:choose>
            <c:when test="${not empty categories}">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Category Name</th>
                                <th>Icon</th>
                                <th>Creator</th>
                                <th>Created Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="category" items="${categories}">
                                <tr>
                                    <td><span class="badge bg-secondary">${category.cateid}</span></td>
                                    <td><strong>${category.catename}</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty category.iconFilename}">
                                                <img src="${pageContext.request.contextPath}/category-icons/${category.iconFilename}" 
                                                     alt="Icon" class="category-icon">
                                            </c:when>
                                            <c:otherwise>
                                                <div class="no-icon">No Icon</div>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty category.user}">
                                                <div>
                                                    <strong>${category.user.fullName}</strong>
                                                    <div class="owner-info">@${category.user.username}</div>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="owner-info">User ID: ${category.userId}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty category.createdAt}">
                                                <small class="text-muted">${category.createdAt}</small>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="owner-info">No information</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="text-center py-5">
                    <i class="fas fa-folder-open fa-4x text-muted mb-3"></i>
                    <h4 class="text-muted">No Categories Found</h4>
                    <p class="text-muted">No categories have been created in the system yet.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
