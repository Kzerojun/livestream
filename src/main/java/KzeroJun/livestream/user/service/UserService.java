package KzeroJun.livestream.user.service;

import KzeroJun.livestream.user.dto.request.SignUpRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {

	ResponseEntity<Void> signUp(SignUpRequest signUpRequest);

}
