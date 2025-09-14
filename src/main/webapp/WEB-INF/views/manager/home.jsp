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
            color: #28a745;
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
            max-width: 1000px;
            margin: 0 auto;
        }
        .page-title {
            font-size: 24px;
            color: #333;
            margin-bottom: 20px;
            font-weight: normal;
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
        <h1 class="page-title">Danh mục của tôi</h1>
        
        <div class="actions-bar">
            <div></div>
            <a href="${pageContext.request.contextPath}/manager/category/add" class="btn btn-success">
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
                        Danh mục của bạn (${categories.size()} danh mục)
                    </div>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tên danh mục</th>
                                <th>Icon</th>
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
                                            <c:when test="${not empty category.createdAt}">
                                                ${category.createdAt}
                                            </c:when>
                                            <c:otherwise>
                                                Không có thông tin
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="actions">
                                        <a href="${pageContext.request.contextPath}/manager/category/view?id=${category.cateid}" 
                                           class="btn">Xem</a>
                                        <a href="${pageContext.request.contextPath}/manager/category/edit?id=${category.cateid}" 
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
                    <p>Bạn chưa tạo danh mục nào. 
                       <a href="${pageContext.request.contextPath}/manager/category/add">Thêm danh mục đầu tiên</a> của bạn!</p>
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
                form.action = '${pageContext.request.contextPath}/manager/category/delete';
                
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
