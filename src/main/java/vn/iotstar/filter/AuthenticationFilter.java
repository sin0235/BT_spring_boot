package vn.iotstar.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;

import java.io.IOException;

@WebFilter(urlPatterns = {"/user/*", "/manager/*", "/admin/*", "/profile", "/profile/*"})
public class AuthenticationFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        // Get session
        HttpSession session = httpRequest.getSession(false);
        User user = null;
        
        if (session != null) {
            user = (User) session.getAttribute("user");
        }
        
        // Check if user is logged in
        if (user == null) {
            // User not logged in, redirect to login
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }
        
        // Check role-based access
        if (!hasAccess(requestURI, contextPath, user.getRoleId())) {
            // Access denied, redirect to appropriate home page or show error
            switch (user.getRoleId()) {
                case 1: // User
                    httpResponse.sendRedirect(contextPath + "/user/home");
                    break;
                case 2: // Manager
                    httpResponse.sendRedirect(contextPath + "/manager/home");
                    break;
                case 3: // Admin
                    httpResponse.sendRedirect(contextPath + "/admin/home");
                    break;
                default:
                    httpResponse.sendRedirect(contextPath + "/login");
                    break;
            }
            return;
        }
        
        // Access granted, continue with request
        chain.doFilter(request, response);
    }
    
    private boolean hasAccess(String requestURI, String contextPath, int roleId) {
        String path = requestURI.substring(contextPath.length());
        
        // Profile access is allowed for all logged-in users
        if (path.startsWith("/profile")) {
            return true;
        }
        
        switch (roleId) {
            case 1: // User role
                return path.startsWith("/user/");
                
            case 2: // Manager role
                return path.startsWith("/manager/") || path.startsWith("/user/");
                
            case 3: // Admin role
                return path.startsWith("/admin/") || path.startsWith("/manager/") || path.startsWith("/user/");
                
            default:
                return false;
        }
    }
    
    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
