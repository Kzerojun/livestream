package KzeroJun.livestream.member.service.implement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import KzeroJun.livestream.auth.dto.request.SignInRequest;
import KzeroJun.livestream.auth.dto.response.SignInResponse;
import KzeroJun.livestream.auth.exception.LoginFailedException;
import KzeroJun.livestream.auth.service.implement.AuthServiceImpl;
import KzeroJun.livestream.auth.dto.request.SignUpRequest;
import KzeroJun.livestream.auth.exception.DuplicatedEmailException;
import KzeroJun.livestream.auth.exception.DuplicatedNicknameException;
import KzeroJun.livestream.global.jwt.provider.JwtTokenProvider;
import KzeroJun.livestream.member.entity.Member;
import KzeroJun.livestream.member.repository.UserRepository;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private AuthenticationManagerBuilder authenticationManagerBuilder;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;


	@InjectMocks
	private AuthServiceImpl authService;


	@DisplayName("회원가입 성공")
	@Test
	void signUp_Success() {
		SignUpRequest signUpRequest = signUpRequest();

		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(userRepository.existsByNickname(anyString())).thenReturn(false);
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

		ResponseEntity<Void> response = authService.signUp(signUpRequest);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		verify(userRepository, times(1)).save(any());
	}

	@DisplayName("중복된 이메일 회원가입 실패")
	@Test
	void signUp_Fail_Duplicated_Email() {
		SignUpRequest signUpRequest = signUpRequest();
		when(userRepository.existsByEmail(anyString())).thenReturn(true);

		assertThatThrownBy(() -> authService.signUp(signUpRequest)).isInstanceOf(
				DuplicatedEmailException.class);

		DuplicatedEmailException response = assertThrows(
				DuplicatedEmailException.class, () -> authService.signUp(signUpRequest));

		assertThat(response.getMessage()).isEqualTo("중복된 이메일 입니다.");
	}

	@DisplayName("중복된 닉네임 회원가입 실패")
	@Test
	void signUp_Fail_Duplicated_Nickname() {
		SignUpRequest signUpRequest = signUpRequest();
		when(userRepository.existsByNickname(anyString())).thenReturn(true);

		assertThatThrownBy(() -> authService.signUp(signUpRequest)).isInstanceOf(
				DuplicatedNicknameException.class);

		DuplicatedNicknameException response = assertThrows(
				DuplicatedNicknameException.class, () -> authService.signUp(signUpRequest));

		assertThat(response.getMessage()).isEqualTo("중복된 닉네임 입니다.");
	}

	@DisplayName("로그인 성공")
	@Test
	void signIn_Success() {
		SignInRequest signInRequest = signInRequest();
		Member mockMember = Member.builder()
				.email(signInRequest.getEmail())
				.password("encryptedPassword")
				.nickname("testUser")
				.build();

		when(userRepository.existsByEmail(signInRequest().getEmail())).thenReturn(false);
		UsernamePasswordAuthenticationToken authenticationToken = signInRequest.toAuthentication();

		AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
		when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
		when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));

		SignInResponse signInResponse = new SignInResponse(null, "accessToken", "refreshToken",
				100L);
		when(jwtTokenProvider.generateToken(any())).thenReturn(signInResponse);

		ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);

		ResponseEntity<SignInResponse> response = authService.signIn(
				signInRequest);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getAccessToken()).isEqualTo("accessToken");
		assertThat(response.getBody().getRefreshToken()).isEqualTo("refreshToken");

		verify(valueOperations).set(anyString(), any(), anyLong(), any());
	}

	@DisplayName("이메일 존재X 로그인 실패")
	@Test
	void signIn_Fail_NoExistEmail() {
		SignInRequest signInRequest = signInRequest();
		when(userRepository.existsByEmail(signInRequest.getEmail())).thenReturn(true);

		assertThatThrownBy(() -> authService.signIn(signInRequest)).isInstanceOf(
				LoginFailedException.class);
	}


	private SignUpRequest signUpRequest() {
		return new SignUpRequest("test@example.com", "password", "testUser");
	}

	private SignInRequest signInRequest() {
		return new SignInRequest("test@example.com", "password");
	}
}