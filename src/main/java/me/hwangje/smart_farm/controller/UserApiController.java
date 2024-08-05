package me.hwangje.smart_farm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.config.jwt.TokenProvider;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RequiredArgsConstructor
@RestController
@Tag(name = "User", description = "사용자 관련 API")
public class UserApiController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/current-user")
    @Operation(summary = "접속한 회원 정보 반환", description = "현재 접속한 회원의 정보를 반환")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok("No user is currently logged in");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            User user = userService.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.ok("User is authenticated but details are not available");
        }
    }
    @PostMapping("/signup")
    @Operation(summary = "회원 가입", description = "새로운 사용자를 등록")
    public ResponseEntity<?> signup(@RequestBody AddUserRequest request) {
        try {
            Long userId = userService.save(request);
            return ResponseEntity.ok().body("User registered successfully. User ID: " + userId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 로그인을 처리하고 JWT 토큰을 반환")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            User user = (User) authentication.getPrincipal();

            String accessToken = tokenProvider.generateToken(user, Duration.ofHours(2));
            String refreshToken = tokenProvider.generateToken(user, Duration.ofDays(14));

            // 여기에 JWT 토큰 생성 로직을 추가할 수 있습니다.
//            refreshTokenService.saveRefreshToken(user.getId(), refreshToken);
//re
//            return ResponseEntity.ok(new CreateAccessTokenResponse(token));
            return null;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }
}
