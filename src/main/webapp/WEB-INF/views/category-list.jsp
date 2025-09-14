<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Danh Sách Danh Mục</title>
    <style>
        body { 
            font-family: Arial, sans-serif; 
            margin: 0;
            padding: 20px; 
            background-color: #f8f9fa;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
        }
        .nav-links {
            margin-bottom: 20px;
        }
        .nav-links a {
            color: #007bff;
            text-decoration: none;
            margin-right: 15px;
            font-size: 14px;
        }
        .nav-links a:hover {
            text-decoration: underline;
        }
        .page-title {
            font-size: 24px;
            color: #333;
            margin-bottom: 20px;
            font-weight: normal;
        }
        .search-section {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 20px;
        }
        .search-input {
            padding: 8px 12px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
            width: 200px;
        }
        .search-input:focus {
            outline: none;
            border-color: #007bff;
        }
        .btn { 
            padding: 8px 16px; 
            background-color: #6c757d; 
            color: white; 
            border: 1px solid #6c757d; 
            border-radius: 4px; 
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 14px;
        }
        .btn:hover { 
            background-color: #5a6268; 
            border-color: #545b62;
        }
        .btn-primary {
            background-color: #007bff;
            border-color: #007bff;
        }
        .btn-primary:hover {
            background-color: #0056b3;
            border-color: #004085;
        }
        .btn-danger {
            background-color: #dc3545;
            border-color: #dc3545;
        }
        .btn-danger:hover {
            background-color: #c82333;
            border-color: #bd2130;
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
            margin: 0;
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
        .actions {
            white-space: nowrap;
        }
        .actions a {
            margin-right: 5px;
            padding: 4px 8px;
            font-size: 12px;
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
        .alert-success {
            background-color: #d4edda;
            border-color: #c3e6cb;
            color: #155724;
        }
        .empty-state {
            text-align: center;
            padding: 50px 20px;
            color: #666;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        .add-btn-container {
            text-align: right;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/">Trang chủ</a>
    </div>

    <div class="container">
        <h1 class="page-title">Quản lý danh mục</h1>
        
        <div class="search-section">
            <input type="text" class="search-input" placeholder="Tìm kiếm..." id="searchInput">
            <button class="btn" onclick="searchCategories()">Search</button>
        </div>
        
        <div class="add-btn-container">
            <a href="${pageContext.request.contextPath}/admin/category/edit" class="btn btn-primary">
                Thêm danh mục
            </a>
        </div>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">
                ${error}
            </div>
        </c:if>
        
        <c:if test="${not empty success}">
            <div class="alert alert-success">
                ${success}
            </div>
        </c:if>
        
        <c:choose>
            <c:when test="${not empty categories}">
                <div class="table-container">
                    <div class="table-header">
                        Danh sách danh mục
                    </div>
                    <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tên danh mục</th>
                            <th>Icon</th>
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
                                                 alt="Icon" class="category-icon" 
                                                 title="Icon: ${category.iconFilename}">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="no-icon">No Icon</div>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="actions">
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
                    <p>Hãy <a href="${pageContext.request.contextPath}/admin/category/edit">thêm danh mục đầu tiên</a> của bạn!</p>
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
        
        function searchCategories() {
            const searchInput = document.getElementById('searchInput');
            const searchText = searchInput.value.toLowerCase().trim();
            const table = document.querySelector('table tbody');
            const rows = table.getElementsByTagName('tr');
            
            for (let i = 0; i < rows.length; i++) {
                const nameCell = rows[i].cells[1]; // Tên danh mục ở cột thứ 2
                if (nameCell) {
                    const nameText = nameCell.textContent.toLowerCase();
                    if (nameText.includes(searchText)) {
                        rows[i].style.display = '';
                    } else {
                        rows[i].style.display = 'none';
                    }
                }
            }
        }
        
        // Tìm kiếm khi người dùng gõ
        document.getElementById('searchInput').addEventListener('input', searchCategories);
        
        // Tìm kiếm khi nhấn Enter
        document.getElementById('searchInput').addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                searchCategories();
            }
        });
    </script>
</body>
</html>