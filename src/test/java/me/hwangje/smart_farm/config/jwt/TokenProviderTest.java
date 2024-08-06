package me.hwangje.smart_farm.config.jwt;

import io.jsonwebtoken.Jwts;
import me.hwangje.smart_farm.domain.Role;
import me.hwangje.smart_farm.domain.User;
import me.hwangje.smart_farm.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProperties jwtProperties;

    @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        //given
        User testUser = userRepository.save(User.builder()
                .email("user@email.com")
                .password("test")
                .role(Role.USER)
                .phoneNumber("01000000000")
                .nickname("똑딱이")
                .build());
        //when
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));
        //then
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);
        String roleString = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);

        Role role = Role.valueOf(roleString);

        assertThat(userId).isEqualTo(testUser.getId());
        assertThat(role).isEqualTo(testUser.getRole());
    }


    // validToken() 검증 테스트
    @DisplayName("validToken(): 만료된 토큰인 때에 유효성 검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        //given
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(1).toMillis()))
                .build()
                .createToken(jwtProperties);

        //when
        boolean result = tokenProvider.validToken(token);

        //then
        assertThat(result).isFalse();
    }

    @DisplayName("validToken(): 유효한 토큰일때에 유효성 검증에 성공한다.")
    @Test
    void validToken_validToken() {
        //given
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties);
        //when
        boolean result = tokenProvider.validToken(token);
        //then
        assertThat(result).isTrue();
    }

    @DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        //given
        String userEmail = "user@email.com";
        Role role = Role.USER;
        String token = JwtFactory.builder()
                .subject(userEmail)
                .claims(Map.of("role", role.name())) // include "ROLE_" prefix
                .build()
                .createToken(jwtProperties);
        //when
        Authentication authentication = tokenProvider.getAuthentication(token);
        //then
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
        assertThat(((UserDetails) authentication.getPrincipal()).getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_" + role.name());
    }

    @DisplayName("getUserId(): 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserId() {
        //given
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);
        //when
        Long userIdByToken = tokenProvider.getUserId(token);
        //then
        assertThat(userIdByToken).isEqualTo(userId);
    }

    @DisplayName("getUserRole(): 토큰으로 유저 role 을 가져올 수 있다.")
    @Test
    void getUserRole() {
        //given
        Role role = Role.USER;
        String token = JwtFactory.builder()
                .claims(Map.of("role", role.name())) // include "ROLE_" prefix
                .build()
                .createToken(jwtProperties);
        //when
        Authentication authentication = tokenProvider.getAuthentication(token);

        //then
        String roleFromAuth = ((UserDetails) authentication.getPrincipal()).getAuthorities().iterator().next().getAuthority();

        assertThat(roleFromAuth).isEqualTo("ROLE_" + role.name());
    }
}
