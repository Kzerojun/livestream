package KzeroJun.livestream.auth.controller;

import KzeroJun.livestream.auth.dto.request.ReissueRequest;
import KzeroJun.livestream.auth.dto.request.SignInRequest;
import KzeroJun.livestream.auth.dto.request.SignUpRequest;
import KzeroJun.livestream.auth.dto.response.SignInResponse;
import KzeroJun.livestream.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
		return authService.signUp(signUpRequest);
	}

	@PostMapping("/signin")
	public ResponseEntity<SignInResponse> signIn(@RequestBody @Valid SignInRequest signInRequest) {
		return authService.signIn(signInRequest);
	}

	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(@Valid @RequestBody ReissueRequest reissueRequest) {
		return authService.reissue(reissueRequest);
	}
}
