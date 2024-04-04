package KzeroJun.livestream.auth.exception;

import KzeroJun.livestream.global.exception.UnauthorizedException;

public class LoginFailedException extends UnauthorizedException {

	private static final String MESSAGE = "로그인 또는 비밀번호가 올바르지 않습니다";

	public LoginFailedException() {
		super(MESSAGE);
	}
}
