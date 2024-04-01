package KzeroJun.livestream.auth.service;

import KzeroJun.livestream.auth.dto.request.ReissueRequest;
import KzeroJun.livestream.auth.dto.request.SignInRequest;
import KzeroJun.livestream.auth.dto.request.SignUpRequest;
import KzeroJun.livestream.auth.dto.response.SignInResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {

	ResponseEntity<Void> signUp(SignUpRequest signUpRequest);

	ResponseEntity<SignInResponse> signIn(SignInRequest signInRequest);

	ResponseEntity<?> reissue(ReissueRequest reissueRequest);

}
