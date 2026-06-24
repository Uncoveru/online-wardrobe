package com.wardrobe.backend.config;

import com.wardrobe.backend.enums.RolePermission;
import com.wardrobe.backend.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class JwtConfig implements WebMvcConfigurer {

    private static final List<String> AUTH_PATHS = List.of(
            "/api/user/login", "/api/user/register", "/api/user/register-operator", "/api/admin/login"
    );

    private static final List<String> ADMIN_PATHS = List.of(
            "/api/admin/"
    );

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(jwtSecret, jwtExpiration);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                     Object handler) {
                if ("OPTIONS".equals(request.getMethod())) return true;
                String path = request.getRequestURI();
                String method = request.getMethod();

                for (String authPath : AUTH_PATHS) {
                    if (path.equals(authPath)) return true;
                }

                boolean isPublicGet = "GET".equals(method) && isPublicGetPath(path);

                if (isPublicGet) return true;

                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    if (jwtUtils().validateToken(token)) {
                        Integer role = jwtUtils().getRole(token);
                        request.setAttribute("userId", jwtUtils().getUserId(token));
                        request.setAttribute("userName", jwtUtils().getUserName(token));
                        request.setAttribute("role", role);

                        for (String adminPath : ADMIN_PATHS) {
                            if (path.startsWith(adminPath)) {
                                if (!RolePermission.fromId(role).isAdmin()) {
                                    response.setStatus(403);
                                    return false;
                                }
                                break;
                            }
                        }
                        return true;
                    }
                }
                response.setStatus(401);
                return false;
            }

            private boolean isPublicGetPath(String path) {
                if (path.startsWith("/images/")) {
                    return true;
                }
                if (path.equals("/api/clothes") || path.equals("/api/clothes/search")
                        || path.equals("/api/types") || path.equals("/api/sizes")) {
                    return true;
                }
                if (path.startsWith("/api/clothes/")) {
                    String tail = path.substring("/api/clothes/".length());
                    return tail.matches("\\d+");
                }
                return false;
            }
        });
    }
}
