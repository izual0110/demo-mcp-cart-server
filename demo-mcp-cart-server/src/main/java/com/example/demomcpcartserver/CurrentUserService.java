package com.example.demomcpcartserver;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public String userId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated principal in security context");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            return jwt.getSubject();
        }

        if (principal instanceof OAuth2AuthenticatedPrincipal oauth2User) {
            String login = oauth2User.getAttribute("login");
            if (login != null && !login.isBlank()) {
                return login;
            }

            String name = oauth2User.getName();
            if (name != null && !name.isBlank()) {
                return name;
            }
        }

        return authentication.getName();
    }
}
