package KzeroJun.livestream.user.service.implement;

import KzeroJun.livestream.user.dto.request.SignInRequest;
import KzeroJun.livestream.user.dto.request.SignUpRequest;
import KzeroJun.livestream.user.entity.User;
import KzeroJun.livestream.user.exception.DuplicatedEmailException;
import KzeroJun.livestream.user.exception.DuplicatedNicknameException;
import KzeroJun.livestream.user.repository.UserRepository;
import KzeroJun.livestream.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	@Override
	public ResponseEntity<Void> signUp(SignUpRequest signUpRequest) {
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new DuplicatedEmailException();
		}

		if (userRepository.existsByNickname(signUpRequest.getNickname())) {
			throw new DuplicatedNicknameException();
		}

		String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());

		User user = User.builder()
				.email(signUpRequest.getEmail())
				.password(encodedPassword)
				.nickname(signUpRequest.getNickname())
				.build();

		userRepository.save(user);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
