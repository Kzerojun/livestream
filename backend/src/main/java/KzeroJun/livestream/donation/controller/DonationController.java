package KzeroJun.livestream.donation.controller;

import KzeroJun.livestream.donation.dto.request.DonationRequest;
import KzeroJun.livestream.donation.dto.response.DonationResponse;
import KzeroJun.livestream.donation.service.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/donation")
@RequiredArgsConstructor
public class DonationController {

	private final DonationService donationService;

	@PostMapping
	public ResponseEntity<DonationResponse> donate(
			@Valid @RequestBody DonationRequest donationRequest) {
		return donationService.donate(donationRequest);
	}
}
