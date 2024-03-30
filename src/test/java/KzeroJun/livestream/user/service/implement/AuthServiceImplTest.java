package KzeroJun.livestream.user.service.implement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import KzeroJun.livestream.auth.service.implement.AuthServiceImpl;
import KzeroJun.livestream.auth.dto.request.SignUpRequest;
import KzeroJun.livestream.user.exception.DuplicatedEmailException;
import KzeroJun.livestream.user.exception.DuplicatedNicknameException;
import KzeroJun.livestream.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthServiceImpl userService;


	@DisplayName("회원가입 성공")
	@Test
	void signUp_Success() {
		SignUpRequest signUpRequest = signUpRequest();

		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(userRepository.existsByNickname(anyString())).thenReturn(false);
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

		ResponseEntity<Void> response = userService.signUp(signUpRequest);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		verify(userRepository, times(1)).save(any());
	}

	@DisplayName("중복된 이메일 회원가입 실패")
	@Test
	void signUp_Fail_Duplicated_Email() {
		SignUpRequest signUpRequest = signUpRequest();
		when(userRepository.existsByEmail(anyString())).thenReturn(true);

		assertThatThrownBy(() -> userService.signUp(signUpRequest)).isInstanceOf(
				DuplicatedEmailException.class);

		DuplicatedEmailException response = assertThrows(
				DuplicatedEmailException.class, () -> userService.signUp(signUpRequest));

		assertThat(response.getMessage()).isEqualTo("중복된 이메일 입니다.");
	}

	@DisplayName("중복된 닉네임 회원가입 실패")
	@Test
	void signUp_Fail_Duplicated_Nickname() {
		SignUpRequest signUpRequest = signUpRequest();
		when(userRepository.existsByNickname(anyString())).thenReturn(true);

		assertThatThrownBy(() -> userService.signUp(signUpRequest)).isInstanceOf(
				DuplicatedNicknameException.class);

		DuplicatedNicknameException response = assertThrows(
				DuplicatedNicknameException.class, () -> userService.signUp(signUpRequest));

		assertThat(response.getMessage()).isEqualTo("중복된 닉네임 입니다.");
	}

	private SignUpRequest signUpRequest() {
		return new SignUpRequest("test@example.com", "password", "testUser");
	}
}