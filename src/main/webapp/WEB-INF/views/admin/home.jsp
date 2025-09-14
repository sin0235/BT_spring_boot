<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${pageTitle}</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f8f9fa;
        }
        .header {
            background: white;
            padding: 15px 20px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .user-info {
            color: #333;
        }
        .user-info .role {
            color: #dc3545;
            font-weight: bold;
        }
        .logout-btn {
            background-color: #dc3545;
            color: white;
            padding: 8px 16px;
            text-decoration: none;
            border-radius: 4px;
            font-size: 14px;
        }
        .logout-btn:hover {
            background-color: #c82333;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
        }
        .page-title {
            font-size: 24px;
            color: #333;
            margin-bottom: 20px;
            font-weight: normal;
        }
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .stat-card {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            text-align: center;
        }
        .stat-number {
            font-size: 32px;
            font-weight: bold;
            color: #007bff;
        }
        .stat-label {
            color: #666;
            margin-top: 5px;
        }
        .actions-bar {
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .btn {
            padding: 8px 16px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            font-size: 14px;
            border: none;
            cursor: pointer;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .btn-success {
            background-color: #28a745;
        }
        .btn-success:hover {
            background-color: #218838;
        }
        .btn-danger {
            background-color: #dc3545;
        }
        .btn-danger:hover {
            background-color: #c82333;
        }
        .table-container {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .table-header {
            background-color: #b8daed;
            padding: 15px 20px;
            font-size: 16px;
            color: #2c3e50;
            border-bottom: 1px solid #a8cde2;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            padding: 12px 20px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }
        th {
            background-color: #f8f9fa;
            font-weight: bold;
            color: #333;
            font-size: 14px;
        }
        td {
            font-size: 14px;
        }
        tr:hover {
            background-color: #f8f9fa;
        }
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
        .empty-state {
            text-align: center;
            padding: 50px 20px;
            color: #666;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        .alert {
            padding: 12px 15px;
            margin-bottom: 20px;
            border-radius: 4px;
            border: 1px solid transparent;
        }
        .alert-error {
            background-color: #f8d7da;
            border-color: #f5c6cb;
            color: #721c24;
        }
        .actions {
            white-space: nowrap;
        }
        .actions a {
            margin-right: 5px;
            padding: 4px 8px;
            font-size: 12px;
        }
        .owner-info {
            font-size: 12px;
            color: #666;
            font-style: italic;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="user-info">
            Xin chào, <strong>${currentUser.fullName}</strong> 
            (<span class="role">${currentUser.roleName}</span>)
        </div>
        <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Đăng xuất</a>
    </div>

    <div class="container">
        <h1 class="page-title">Bảng điều khiển Admin</h1>
        
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-number">${userCount}</div>
                <div class="stat-label">Người dùng</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">${managerCount}</div>
                <div class="stat-label">Quản lý</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">${adminCount}</div>
                <div class="stat-label">Quản trị viên</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">${categories.size()}</div>
                <div class="stat-label">Tổng danh mục</div>
            </div>
        </div>
        
        <div class="actions-bar">
            <h2>Tất cả danh mục trong hệ thống</h2>
            <a href="${pageContext.request.contextPath}/admin/category/add" class="btn btn-success">
                + Thêm danh mục mới
            </a>
        </div>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">
                ${error}
            </div>
        </c:if>
        
        <c:choose>
            <c:when test="${not empty categories}">
                <div class="table-container">
                    <div class="table-header">
                        Danh sách tất cả danh mục (${categories.size()} danh mục)
                    </div>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tên danh mục</th>
                                <th>Icon</th>
                                <th>Người tạo</th>
                                <th>Ngày tạo</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="category" items="${categories}">
                                <tr>
                                    <td>${category.cateid}</td>
                                    <td>${category.catename}</td>
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
                                                ${category.user.fullName}
                                                <div class="owner-info">(${category.user.username})</div>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="owner-info">User ID: ${category.userId}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty category.createdAt}">
                                                ${category.createdAt}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="owner-info">Không có thông tin</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="actions">
                                        <a href="${pageContext.request.contextPath}/admin/category/view?id=${category.cateid}" 
                                           class="btn">Xem</a>
                                        <a href="${pageContext.request.contextPath}/admin/category/edit?id=${category.cateid}" 
                                           class="btn">Cập nhật</a>
                                        <a href="javascript:void(0)" 
                                           onclick="deleteCategory(${category.cateid}, '${category.catename}')"
                                           class="btn btn-danger">Xóa</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <h2>Chưa có danh mục nào</h2>
                    <p>Hệ thống chưa có danh mục nào. 
                       <a href="${pageContext.request.contextPath}/admin/category/add">Thêm danh mục đầu tiên</a>!</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <script>
        function deleteCategory(id, name) {
            if (confirm('Bạn có chắc chắn muốn xóa danh mục "' + name + '"?\nHành động này không thể hoàn tác!')) {
                // Create form and submit
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/admin/category/delete';
                
                const idInput = document.createElement('input');
                idInput.type = 'hidden';
                idInput.name = 'id';
                idInput.value = id;
                
                form.appendChild(idInput);
                document.body.appendChild(form);
                form.submit();
            }
        }
    </script>
</body>
</html>
