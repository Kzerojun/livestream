package KzeroJun.livestream.donation.dto.response;

import KzeroJun.livestream.global.dto.ResponseDto;
import lombok.Getter;

@Getter
public class DonationResponse extends ResponseDto {

	private static final String MESSAGE = "Donation Success";

	private DonationResponse() {
		super(MESSAGE);
	}

	public static DonationResponse success() {
		return new DonationResponse();
	}
}
