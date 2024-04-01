package KzeroJun.livestream.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class ReissueRequest {

	@NotEmpty(message = "accessToken을 입력해주세요.")
	private String accessToken;

	@NotEmpty(message = "refreshToken 입력해주세요.")
	private String refreshToken;

}
