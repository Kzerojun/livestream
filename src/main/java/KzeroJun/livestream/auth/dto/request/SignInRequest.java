package KzeroJun.livestream.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@Setter
public class SignInRequest {

	@NotBlank(message = "아이디를 입력해주세요")
	@Pattern(regexp = "^[0-9A-Za-z]{6,15}$", message = "숫자와 영문만 가능합니다. 길이는 6자 이상 15자이하입니다.")
	private String loginId;

	@NotBlank(message = "비밀번호를 입력해주세요")
	private String password;

	public UsernamePasswordAuthenticationToken toAuthentication() {
		return new UsernamePasswordAuthenticationToken(loginId, password);
	}
}
