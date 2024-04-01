package KzeroJun.livestream.auth.service.implement;

import KzeroJun.livestream.auth.dto.request.ReissueRequest;
import KzeroJun.livestream.auth.dto.request.SignInRequest;
import KzeroJun.livestream.auth.dto.request.SignUpRequest;
import KzeroJun.livestream.auth.dto.response.SignInResponse;
import KzeroJun.livestream.auth.exception.LoginFailedException;
import KzeroJun.livestream.global.exception.NoPermissionTokenException;
import KzeroJun.livestream.global.jwt.provider.JwtTokenProvider;
import KzeroJun.livestream.member.entity.Member;
import KzeroJun.livestream.auth.exception.DuplicatedEmailException;
import KzeroJun.livestream.auth.exception.DuplicatedNicknameException;
import KzeroJun.livestream.member.repository.UserRepository;
import KzeroJun.livestream.auth.service.AuthService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	private final JwtTokenProvider jwtTokenProvider;

	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public ResponseEntity<Void> signUp(SignUpRequest signUpRequest) {
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new DuplicatedEmailException();
		}

		if (userRepository.existsByNickname(signUpRequest.getNickname())) {
			throw new DuplicatedNicknameException();
		}

		String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());

		Member user = Member.builder()
				.email(signUpRequest.getEmail())
				.password(encodedPassword)
				.nickname(signUpRequest.getNickname())
				.build();

		userRepository.save(user);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Override
	public ResponseEntity<SignInResponse> signIn(SignInRequest signInRequest) {
		userRepository.findByEmail(signInRequest.getEmail())
				.orElseThrow(LoginFailedException::new);

		UsernamePasswordAuthenticationToken authenticationToken = signInRequest.toAuthentication();
		Authentication authentication = authenticationManagerBuilder.getObject()
				.authenticate(authenticationToken);

		SignInResponse signInResponse = jwtTokenProvider.generateToken(authentication);
		redisTemplate.opsForValue().set("RT:" + authentication.getName(),
				signInResponse.getRefreshToken(),
				signInResponse.getRefreshTokenExpirationTIme(), TimeUnit.MILLISECONDS);

		return ResponseEntity.ok().body(signInResponse);
	}

	public ResponseEntity<?> reissue(ReissueRequest reissue) {
		if (jwtTokenProvider.validate(reissue.getRefreshToken()) == null) {
			throw new NoPermissionTokenException();
		}

		Authentication authentication = jwtTokenProvider.getAuthentication(
				reissue.getAccessToken());

		String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + authentication.getName());
		if (!refreshToken.equals(reissue.getRefreshToken())) {
			throw new NoPermissionTokenException();
		}

		SignInResponse signInResponse = jwtTokenProvider.generateToken(authentication);
		redisTemplate.opsForValue().set("RT:" + authentication.getName(),
				signInResponse.getRefreshToken(),
				signInResponse.getRefreshTokenExpirationTIme(), TimeUnit.MILLISECONDS);

		return ResponseEntity.ok().build();
	}

}
