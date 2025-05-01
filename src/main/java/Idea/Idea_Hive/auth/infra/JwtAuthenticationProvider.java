package Idea.Idea_Hive.auth.infra;

import Idea.Idea_Hive.auth.service.JwtProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import javax.security.auth.Subject;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (!supports(authentication.getClass())) {
            return null;
        }

        String atk = (String) authentication.getPrincipal();

        /* JwtToken 검증, 실패 시 예외 발생 */
        if (atk != null) {
            jwtProvider.validateToken(atk);
            String email = jwtProvider.getEmail(atk);

            return new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    null
            );
        } else {
            Authentication failToken =  new UsernamePasswordAuthenticationToken(null, null, null);
            failToken.setAuthenticated(false);
            return failToken;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
