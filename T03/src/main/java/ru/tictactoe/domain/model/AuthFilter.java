package ru.tictactoe.domain.model;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import ru.tictactoe.domain.service.AuthService;

import java.io.IOException;
import java.util.UUID;

/**
 * Пользовательский фильтр для цепочки фильтров Spring Security (будет добавлен в файле конфигурации)
 * GenericFilterBean — это простая реализация реализации javax.servlet.Filter, поддерживающая Spring
 */
public class AuthFilter extends GenericFilterBean {

    private final AuthService authService;

    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        if (path.contains("/auth") || path.contains("/registration") || path.contains("/perform_login")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Basic ")) {
            unauthorized(httpResponse);
            return;
        }

        String base64Credentials = authHeader.substring("Basic ".length()).trim();
        UUID userId = authService.authorization(base64Credentials);

        try {
            if (userId != null) {
                // Установка аутентификации в SecurityContext, чтобы авторизация не исчезала из заголовка запроса
                Authentication auth = new UsernamePasswordAuthenticationToken(userId.toString(), null, null);
                SecurityContextHolder.getContext().setAuthentication(auth);
                httpRequest.setAttribute("user", userId);

                chain.doFilter(request, response);
            } else {
                unauthorized(httpResponse);
            }
        } catch (IllegalArgumentException exception) {
            unauthorized(httpResponse);
        }
    }

    private void unauthorized(HttpServletResponse response) throws IOException {
        response.setHeader("WWW-Authenticate", "Basic realm=\"Realm name\"");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized");
    }

}
