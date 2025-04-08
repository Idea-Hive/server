package Idea.Idea_Hive.auth.service;

import Idea.Idea_Hive.member.entity.repository.MemberJpaRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OAuth2 로그인 시 사용자 정보를 가져오는 역할 수행
 */
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // google or github
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        String email = (String) attributes.get("email"); // Email 추출

        String userNameAttributeName;

        if ("github".equals(registrationId) && email == null) {
            email = fetchGithubEmail(userRequest);
            userNameAttributeName = "login";
        } else if ("google".equals(registrationId) && email == null) {
            // google의 경우 email은 기본으로 제공됨
            // attributes에서 "email", "name" 등 접근 가능
            userNameAttributeName = "sub";
        } else { // default
            userNameAttributeName = "sub";
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                userNameAttributeName // google은 sub, github은 login 사용 (주의!)
        );
    }

    // fetch Email From github
    private String fetchGithubEmail(OAuth2UserRequest userRequest) {
        String uri = "https://api.github.com/user/emails";
        String token = userRequest.getAccessToken().getTokenValue();

        /* Http Message */
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                uri, HttpMethod.GET, request, new ParameterizedTypeReference<>() {}
        );

        return response.getBody().stream()
                .filter(email -> Boolean.TRUE.equals(email.get("primary")))
                .map(email -> (String) email.get("email"))
                .findFirst()
                .orElse(null);
    }

}
