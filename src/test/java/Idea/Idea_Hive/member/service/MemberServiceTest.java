package Idea.Idea_Hive.member.service;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.dto.request.ProfileUpdateRequest;
import Idea.Idea_Hive.member.entity.dto.request.SignUpRequest;
import Idea.Idea_Hive.member.entity.dto.response.MemberInfoResponse;
import Idea.Idea_Hive.member.entity.dto.response.SignUpResponse;
import Idea.Idea_Hive.member.entity.repository.MemberRepository;
import Idea.Idea_Hive.redis.RedisDao;
import Idea.Idea_Hive.skillstack.entity.SkillStack;
import Idea.Idea_Hive.skillstack.entity.repository.SkillStackJpaRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SkillStackJpaRepo skillStackJpaRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisDao redisDao;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("test@test.com")
                .name("testuser")
                .password("password")
                .build();
    }

    @Test
    @DisplayName("회원가입에 성공합니다.")
    void signUp() {
        // given
        SignUpRequest request = new SignUpRequest("test@test.com", "testuser", "password", true, true, false);
        when(memberRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // when
        SignUpResponse response = memberService.signUp(request);

        // then
        assertThat(response.email()).isEqualTo("test@test.com");
        assertThat(response.name()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입 시 예외가 발생합니다.")
    void signUpWithExistingEmail() {
        // given
        SignUpRequest request = new SignUpRequest("test@test.com", "testuser", "password", true, true, false);
        when(memberRepository.existsByEmail("test@test.com")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.signUp(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }

    @Test
    @DisplayName("사용자 정보를 성공적으로 조회합니다.")
    void getUserInfo() {
        // given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@test.com", null, Collections.emptyList())
        );
        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));

        // when
        MemberInfoResponse response = memberService.getUserInfo();

        // then
        assertThat(response.email()).isEqualTo("test@test.com");
        assertThat(response.name()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("프로필을 성공적으로 업데이트합니다.")
    void updateProfile() {
        // given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@test.com", null, Collections.emptyList())
        );
        ProfileUpdateRequest request = new ProfileUpdateRequest("newuser", "newjob", 1, Collections.singletonList(1L));
        SkillStack skillStack = new SkillStack("category", "skill");
        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));
        when(skillStackJpaRepo.findAllByIdIn(Collections.singletonList(1L))).thenReturn(Collections.singletonList(skillStack));

        // when
        MemberInfoResponse response = memberService.updateProfile(request);

        // then
        assertThat(response.name()).isEqualTo("newuser");
        assertThat(response.job()).isEqualTo("newjob");
        assertThat(response.career()).isEqualTo(1);
        assertThat(response.SkillStacks()).hasSize(1);
    }
}
