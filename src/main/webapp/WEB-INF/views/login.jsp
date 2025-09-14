<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng Nhập</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f8f9fa;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
        .login-container {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            overflow: hidden;
            width: 100%;
            max-width: 400px;
        }
        .login-header {
            background-color: #b8daed;
            padding: 20px;
            text-align: center;
        }
        .login-header h1 {
            color: #2c3e50;
            margin: 0;
            font-size: 24px;
            font-weight: normal;
        }
        .login-content {
            padding: 30px;
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
        input[type="text"], input[type="password"] {
            width: 100%;
            padding: 12px 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 14px;
            background-color: #fff;
        }
        input[type="text"]:focus, input[type="password"]:focus {
            outline: none;
            border-color: #007bff;
            box-shadow: 0 0 0 2px rgba(0,123,255,.25);
        }
        .btn {
            width: 100%;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            font-weight: bold;
        }
        .btn:hover {
            background-color: #0056b3;
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
        .demo-info {
            margin-top: 20px;
            padding: 15px;
            background-color: #f8f9fa;
            border-radius: 4px;
            border-left: 4px solid #007bff;
        }
        .demo-info h4 {
            margin: 0 0 10px 0;
            color: #333;
        }
        .demo-accounts {
            font-size: 12px;
            color: #666;
        }
        .demo-accounts div {
            margin: 5px 0;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="login-header">
            <h1>Đăng Nhập Hệ Thống</h1>
        </div>
        
        <div class="login-content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    ${error}
                </div>
            </c:if>
            
            <c:if test="${not empty message}">
                <div class="alert alert-success">
                    ${message}
                </div>
            </c:if>
            
            <form method="post" action="${pageContext.request.contextPath}/login">
                <div class="form-group">
                    <label for="username">Tên đăng nhập</label>
                    <input type="text" id="username" name="username" 
                           value="${username}" required 
                           placeholder="Nhập tên đăng nhập">
                </div>
                
                <div class="form-group">
                    <label for="password">Mật khẩu</label>
                    <input type="password" id="password" name="password" 
                           required placeholder="Nhập mật khẩu">
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn">Đăng Nhập</button>
                </div>
            </form>
            
            <div class="demo-info">
                <h4>Tài khoản demo:</h4>
                <div class="demo-accounts">
                    <div><strong>User:</strong> user1 / password123</div>
                    <div><strong>Manager:</strong> manager1 / password123</div>
                    <div><strong>Admin:</strong> admin1 / password123</div>
                </div>
            </div>
        </div>
    </div>

    <script>
        // Focus on username field when page loads
        window.onload = function() {
            document.getElementById('username').focus();
        };
        
        // Clear message after 5 seconds
        setTimeout(function() {
            var message = document.querySelector('.alert-success');
            if (message) {
                message.style.display = 'none';
            }
        }, 5000);
    </script>
</body>
</html>
