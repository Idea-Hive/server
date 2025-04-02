package Idea.Idea_Hive.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * DI의 경우 @Autowired를 통한 필드 주입이 아닌 생성자 주입 방식으로 하는게 좋을 것 같습니다!!
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /* 비밀번호 해싱(암호화)를 위한 객체 빈 등록 */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws  Exception {
        http
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
                });
        return http.build();
    }
}
