package Idea.Idea_Hive.security;

import Idea.Idea_Hive.auth.handler.CustomOAuth2SuccessHandler;
import Idea.Idea_Hive.auth.infra.BearerAuthorizationExtractor;
import Idea.Idea_Hive.auth.infra.JwtAuthenticationFilter;
import Idea.Idea_Hive.auth.infra.JwtAuthenticationProvider;
import Idea.Idea_Hive.auth.service.CustomOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
    private final CustomOauth2UserService customOauth2UserService;
    private final AuthenticationConfiguration authenticationConfiguration;

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final BearerAuthorizationExtractor extractor;

    // 1번
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
        return new JwtAuthenticationFilter("/**", extractor, authenticationManager);
    }

    @Value("${frontend.url}")
    private String frontendUrl;


    /* 비밀번호 해싱(암호화)를 위한 객체 빈 등록, 순환 참조 때문에 따로 뺐습니다! */
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws  Exception {

        AuthenticationManager authenticationManager = authenticationManager();
        JwtAuthenticationFilter jwtAuthenticationFilter = jwtAuthenticationFilter(authenticationManager);

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable) // default formlogin unactive
                .httpBasic(AbstractHttpConfigurer::disable) // httpBasic unactive
                .csrf(AbstractHttpConfigurer::disable) /* todo: CSRF 임시 비활성, 프론트 협의 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // IF_REQUIRED : 다른 API 요청에 대해서는 JWT 인증 사용
                ) // 세션 Stateless 설정 (JWT 사용 예정)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(jwtAuthenticationProvider)
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers("/api/member/signup").permitAll()
                            .requestMatchers("/api/auth/login").permitAll()
                            .requestMatchers("/api/auth/refresh").permitAll()
                            .requestMatchers("/api/email/signup/send").permitAll()
                            .requestMatchers("/api/email/signup/verify").permitAll()
                            .requestMatchers("/api/email/password-reset/send").permitAll()
                            .requestMatchers("/api/email/password-reset/verify").permitAll()
                            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                            .requestMatchers("/api/project/search/**").permitAll()
                            .anyRequest().authenticated();
                })
                /* todo: OAuth2 */
                .oauth2Login(ouath -> ouath
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOauth2UserService))
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













