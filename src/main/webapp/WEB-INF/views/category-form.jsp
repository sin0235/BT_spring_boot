<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Category Form</title>
    <style>
        body { 
            font-family: Arial, sans-serif; 
            margin: 0;
            padding: 20px;
            background-color: #f8f9fa;
        }
        .container {
            max-width: 500px;
            margin: 0 auto;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .form-header {
            background-color: #b8daed;
            padding: 15px 25px;
            margin: 0;
            font-size: 16px;
            font-weight: normal;
            color: #2c3e50;
            border-bottom: 1px solid #a8cde2;
        }
        .form-content {
            padding: 25px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label { 
            display: block; 
            margin-bottom: 8px; 
            font-weight: bold;
            color: #333;
            font-size: 14px;
        }
        input[type="text"] { 
            width: 100%; 
            padding: 10px 12px; 
            border: 1px solid #ddd; 
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 14px;
            background-color: #fff;
        }
        input[type="text"]:focus {
            outline: none;
            border-color: #007bff;
            box-shadow: 0 0 0 2px rgba(0,123,255,.25);
        }
        input[type="file"] { 
            width: 100%; 
            padding: 8px; 
            border: 1px solid #ddd; 
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 14px;
            background-color: #fff;
        }
        .current-icon {
            margin: 10px 0;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 4px;
            border: 1px solid #dee2e6;
        }
        .current-icon img {
            max-width: 100px;
            max-height: 100px;
            border-radius: 4px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .file-info {
            font-size: 12px;
            color: #666;
            margin-top: 5px;
        }
        .btn { 
            padding: 8px 16px; 
            background-color: #6c757d; 
            color: white; 
            border: 1px solid #6c757d; 
            border-radius: 4px; 
            cursor: pointer; 
            margin-right: 10px;
            font-size: 14px;
            text-decoration: none;
            display: inline-block;
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
        .nav-links {
            margin-bottom: 20px;
            padding: 0 25px;
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
    </style>
</head>
<body>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/admin/category">Danh sách danh mục</a>
    </div>

    <div class="container">
        <div class="form-header">
            ${category != null ? 'Chỉnh Sửa Danh Mục' : 'Thêm danh mục'}
        </div>
        
        <div class="form-content">
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
            
            <form method="post" action="${pageContext.request.contextPath}/admin/category/${category != null ? 'update' : 'create'}"
                  enctype="multipart/form-data">
            <c:if test="${category != null}">
                <input type="hidden" name="cateid" value="${category.cateid}" />
            </c:if>
            
                <div class="form-group">
                    <label for="catename">Tên danh mục</label>
                    <input type="text" id="catename" name="catename" value="${category.catename}" required 
                           placeholder=""/>
                </div>
                
                <div class="form-group">
                    <label for="icon">Icon</label>
                
                <c:if test="${category != null && category.iconFilename != null}">
                    <div class="current-icon">
                        <strong>Icon hiện tại:</strong><br>
                        <img src="${pageContext.request.contextPath}/category-icons/${category.iconFilename}" 
                             alt="Current icon" />
                        <p><small>Tên file: ${category.iconFilename}</small></p>
                    </div>
                </c:if>
                
                <input type="file" id="icon" name="icon" accept="image/*"/>
                <div class="file-info">
                    Chấp nhận: .jpg, .jpeg, .png, .gif, .bmp (Tối đa 2MB)
                    <c:if test="${category != null && category.iconFilename != null}">
                        <br><em>Để trống nếu không muốn thay đổi icon</em>
                    </c:if>
                </div>
            </div>
            
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">
                        Submit
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/category" class="btn">
                        Cancel
                    </a>
                </div>
            </form>
        </div>
    </div>

    <script>
        document.getElementById('icon').addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const fileSize = (file.size / 1024 / 1024).toFixed(2);
                if (fileSize > 2) {
                    alert('File quá lớn! Vui lòng chọn file nhỏ hơn 2MB.');
                    e.target.value = '';
                    return;
                }
                
                // Preview image
                const reader = new FileReader();
                reader.onload = function(e) {
                    // Remove existing preview
                    const existingPreview = document.getElementById('iconPreview');
                    if (existingPreview) {
                        existingPreview.remove();
                    }
                    
                    // Create new preview
                    const preview = document.createElement('div');
                    preview.id = 'iconPreview';
                    preview.className = 'current-icon';
                    preview.innerHTML = '<strong>Xem trước:</strong><br><img src="' + e.target.result + '" alt="Preview" style="max-width: 100px; max-height: 100px;">';
                    
                    document.getElementById('icon').parentNode.appendChild(preview);
                };
                reader.readAsDataURL(file);
            }
        });
    </script>
</body>
</html>