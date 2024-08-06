package me.hwangje.smart_farm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.config.jwt.TokenProvider;
import me.hwangje.smart_farm.domain.Role;
import me.hwangje.smart_farm.domain.User;
import me.hwangje.smart_farm.dto.AddUserRequest;
import me.hwangje.smart_farm.dto.CreateAccessTokenResponse;
import me.hwangje.smart_farm.dto.LoginRequest;
import me.hwangje.smart_farm.service.RefreshTokenService;
import me.hwangje.smart_farm.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "User", description = "사용자 관련 API")
public class UserApiController {

    private final UserService userService;

    @PostMapping("/user")
    public String signup(AddUserRequest request) {
        userService.save(request);
        return request.getNickname();
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler()
                .logout(request,
                        response,
                        SecurityContextHolder.getContext().getAuthentication()
                );
    }

    @GetMapping("/users")
    public List<User> findAll() throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("인증되지 않은 사용자입니다.");
        }

        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);

        if (hasRole(authentication, Role.ADMIN)) {
            return userService.findAll();
        } else if (hasRole(authentication, Role.MANAGER)) {
            return userService.findByGroup(currentUser.getGroupId());
        } else {
            return Collections.singletonList(currentUser);
        }
    }

    private boolean hasRole(Authentication authentication, Role role) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(role.name()));
    }
}
