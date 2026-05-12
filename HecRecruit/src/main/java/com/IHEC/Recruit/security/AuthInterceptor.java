package com.IHEC.Recruit.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final CurrentUser currentUser;

    public AuthInterceptor(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String uri = request.getRequestURI();

        if (uri.startsWith("/candidat") && !currentUser.isCandidat()) {
            response.sendRedirect("/login/candidat");
            return false;
        }
        if (uri.startsWith("/entreprise") && !currentUser.isEntreprise()) {
            response.sendRedirect("/login/entreprise");
            return false;
        }
        return true;
    }
}
