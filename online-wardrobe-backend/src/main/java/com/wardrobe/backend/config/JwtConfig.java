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

/**
 * JWT 拦截器配置：Token 校验 + 角色权限控制
 */
@Configuration
public class JwtConfig implements WebMvcConfigurer {

    // 白名单路径，无需 Token
    private static final List<String> AUTH_PATHS = List.of("/api/user/login", "/api/user/register",
            "/api/user/register-operator", "/api/admin/login");

    // 需要管理员角色的路径前缀
    private static final List<String> ADMIN_PATHS = List.of("/api/admin/");

    // JWT 签名密钥（application.yml: jwt.secret）
    @Value("${jwt.secret}")
    private String jwtSecret;

    // JWT 过期时间，毫秒（application.yml: jwt.expiration）
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // 注入 JwtUtils Bean，供拦截器使用
    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(jwtSecret, jwtExpiration);
    }

    // 注册全局拦截器：白名单 → Token 校验 → 公开 GET → 管理员角色
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                // OPTIONS 预检放行
                if ("OPTIONS".equals(request.getMethod()))
                    return true;
                String path = request.getRequestURI();
                String method = request.getMethod();

                // 白名单直接放行
                for (String authPath : AUTH_PATHS) {
                    if (path.equals(authPath))
                        return true;
                }

                // 解析 Bearer Token，校验通过后把用户信息写入 request
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    if (jwtUtils().validateToken(token)) {
                        Integer role = jwtUtils().getRole(token);
                        request.setAttribute("userId", jwtUtils().getUserId(token));
                        request.setAttribute("userName", jwtUtils().getUserName(token));
                        request.setAttribute("role", role);
                    }
                }

                // 公开 GET 接口（商品列表/详情/类型/尺码/图片），未登录也可访问
                boolean isPublicGet = "GET".equals(method) && isPublicGetPath(path);
                if (isPublicGet)
                    return true;

                // 无有效 Token → 401
                if (request.getAttribute("userId") == null) {
                    response.setStatus(401);
                    return false;
                }

                // 管理员路径校验角色，非管理员 → 403
                Integer role = (Integer) request.getAttribute("role");
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

            // 判断是否公开 GET 路径
            private boolean isPublicGetPath(String path) {
                if (path.startsWith("/images/"))
                    return true;
                if (path.equals("/api/clothes") || path.equals("/api/clothes/search") || path.equals("/api/types")
                        || path.equals("/api/sizes"))
                    return true;
                if (path.startsWith("/api/clothes/")) {
                    String tail = path.substring("/api/clothes/".length());
                    return tail.matches("\\d+");
                }
                return false;
            }
        });
    }
}
