package com.example.demomcpcartserver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrentUserServiceTests {

    private final CurrentUserService currentUserService = new CurrentUserService();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsSubjectForJwtPrincipal() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("jwt-user")
                .claim("scope", "read")
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));

        assertThat(currentUserService.userId()).isEqualTo("jwt-user");
    }

    @Test
    void returnsGithubLoginForOauth2Principal() {
        DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                java.util.Set.of(new OAuth2UserAuthority(Map.of("login", "octocat", "id", 1))),
                Map.of("login", "octocat", "id", 1),
                "login");
        OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(oauthUser, oauthUser.getAuthorities(), "github");
        SecurityContextHolder.getContext().setAuthentication(token);

        assertThat(currentUserService.userId()).isEqualTo("octocat");
    }

    @Test
    void throwsWhenUnauthenticated() {
        assertThatThrownBy(() -> currentUserService.userId())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No authenticated principal");
    }
}
