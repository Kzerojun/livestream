package KzeroJun.livestream.donation.service;

import KzeroJun.livestream.donation.dto.request.DonationRequest;
import KzeroJun.livestream.donation.dto.response.DonationResponse;
import KzeroJun.livestream.member.entity.Member;
import org.springframework.http.ResponseEntity;

public interface DonationService {

	ResponseEntity<DonationResponse> donate(DonationRequest donationRequest);
}
