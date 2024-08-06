//package me.hwangje.smart_farm.config;
//
//import lombok.RequiredArgsConstructor;
//import me.hwangje.smart_farm.service.UserDetailService;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
//
//@RequiredArgsConstructor
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class WebSecurityConfig {
//    private final UserDetailService userService;
//
//    // 스프링 시큐리티 기능 비활성화
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring()
//                .requestMatchers(toH2Console())
//                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**");
//
//    }
//
//    // 특정 Http 요청에 대한 웹 기반 보안 구성
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                .authorizeRequests(auth -> auth
//                        .requestMatchers(
//                                new AntPathRequestMatcher("/login"),
//                                new AntPathRequestMatcher("/user"),
//                                new AntPathRequestMatcher("/signup"),
//                                new AntPathRequestMatcher("/swagger-ui/**"),
//                                new AntPathRequestMatcher("/v3/api-docs/**"),
//                                new AntPathRequestMatcher("/swagger-ui.html"),
//                                new AntPathRequestMatcher("/api-docs/**")
//                        ).permitAll()
//                        .anyRequest().authenticated())
//                .formLogin(formLogin -> formLogin
//                        .loginPage("/login")
//                        .defaultSuccessUrl("/api/articles")
//                )
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/login")
//                        .invalidateHttpSession(true)
//                )
//                .csrf(AbstractHttpConfigurer::disable)
//                .build();
//    }
//
//    // 인증 관리자 관련 설정
//    @Bean
//    public AuthenticationManager authenticationManager(
//            HttpSecurity http,
//            BCryptPasswordEncoder bCryptPasswordEncoder,
//            UserDetailService userDetailService
//    ) throws Exception {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userService); // 사용자 정보 서비스 설정
//        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
//        return new ProviderManager(authProvider);
//    }
//
//    // 패스워드 인코더로 사용할 빈 등록
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//}
