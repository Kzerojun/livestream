package KzeroJun.livestream.auth.service;

import KzeroJun.livestream.auth.dto.request.SignInRequest;
import KzeroJun.livestream.auth.dto.request.SignUpRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {

	ResponseEntity<Void> signUp(SignUpRequest signUpRequest);

}
