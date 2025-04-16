package Idea.Idea_Hive.security;

import Idea.Idea_Hive.auth.handler.CustomOAuth2SuccessHandler;
import Idea.Idea_Hive.auth.service.CustomOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * DI의 경우 @Autowired를 통한 필드 주입이 아닌 생성자 주입 방식으로 하는게 좋을 것 같습니다!!
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Value("${frontend.url}")
    private String frontendUrl;


    /* 비밀번호 해싱(암호화)를 위한 객체 빈 등록 */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws  Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable) // default formlogin unactive
                .httpBasic(AbstractHttpConfigurer::disable) // httpBasic unactive
                .csrf(AbstractHttpConfigurer::disable) /* todo: CSRF 임시 비활성, 프론트 협의 */
                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ) // 세션 Stateless 설정 (JWT 사용 예정)
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .anyRequest().permitAll();
                            /* todo: URLs */
                })
                /* todo: OAuth2 */
                .oauth2Login(ouath -> ouath
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(new CustomOauth2UserService()))
                        .successHandler(customOAuth2SuccessHandler)); // todo: 로그인 성공 시 Redirect 페이지

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                frontendUrl
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "FETCH"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}













