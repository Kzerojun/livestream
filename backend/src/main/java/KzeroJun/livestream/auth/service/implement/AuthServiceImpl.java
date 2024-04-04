package KzeroJun.livestream.auth.service.implement;

import KzeroJun.livestream.auth.dto.request.LogoutRequest;
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
import KzeroJun.livestream.member.repository.MemberRepository;
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

	private final MemberRepository memberRepository;

	private final PasswordEncoder passwordEncoder;

	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	private final JwtTokenProvider jwtTokenProvider;

	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public ResponseEntity<Void> signUp(SignUpRequest signUpRequest) {
		if (memberRepository.existsByLoginId(signUpRequest.getLoginId())) {
			throw new DuplicatedEmailException();
		}

		if (memberRepository.existsByNickname(signUpRequest.getNickname())) {
			throw new DuplicatedNicknameException();
		}

		String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());

		Member user = Member.builder()
				.loginId(signUpRequest.getLoginId())
				.password(encodedPassword)
				.nickname(signUpRequest.getNickname())
				.build();

		memberRepository.save(user);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Override
	public ResponseEntity<SignInResponse> signIn(SignInRequest signInRequest) {

		if (!memberRepository.existsByLoginId(signInRequest.getLoginId())) {
			throw new LoginFailedException();
		}

		UsernamePasswordAuthenticationToken authenticationToken = signInRequest.toAuthentication();
		Authentication authentication = authenticationManagerBuilder.getObject()
				.authenticate(authenticationToken);

		SignInResponse signInResponse = jwtTokenProvider.generateToken(authentication);
		redisTemplate.opsForValue().set("RT:" + authentication.getName(),
				signInResponse.getRefreshToken(),
				signInResponse.getRefreshTokenExpirationTIme(), TimeUnit.MILLISECONDS);

		return ResponseEntity.ok().body(signInResponse);
	}

	@Override
	public ResponseEntity<?> reissue(ReissueRequest reissue) {
		if (!jwtTokenProvider.validate(reissue.getRefreshToken())) {
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

	@Override
	public ResponseEntity<Void> logout(LogoutRequest logoutRequest) {

		if (!jwtTokenProvider.validate(logoutRequest.getAccessToken())) {
			System.out.println("인증 실패");
			return ResponseEntity.badRequest().build();
		}

		Authentication authentication = jwtTokenProvider.getAuthentication(
				logoutRequest.getAccessToken());

		//Redis 에서 해당 User email 로 저장된 Refresh Token 이 있는지
		if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
			// Refresh Token 삭제
			redisTemplate.delete("RT:" + authentication.getName());
		}

		Long expiration = jwtTokenProvider.getExpiration(logoutRequest.getAccessToken());
		redisTemplate.opsForValue()
				.set(logoutRequest.getRefreshToken(), "logout", expiration, TimeUnit.MILLISECONDS);

		return ResponseEntity.ok().build();
	}
}
