<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Chuyển hướng trực tiếp đến trang đăng nhập
    response.sendRedirect(request.getContextPath() + "/login");
%>