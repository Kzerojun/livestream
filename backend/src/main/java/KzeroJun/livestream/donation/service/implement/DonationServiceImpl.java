package KzeroJun.livestream.donation.service.implement;

import KzeroJun.livestream.donation.dto.request.DonationRequest;
import KzeroJun.livestream.donation.dto.response.DonationResponse;
import KzeroJun.livestream.donation.entity.Donation;
import KzeroJun.livestream.donation.repository.DonationRepository;
import KzeroJun.livestream.donation.service.DonationService;
import KzeroJun.livestream.member.entity.Member;
import KzeroJun.livestream.member.exception.NoExistedMember;
import KzeroJun.livestream.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

	private final DonationRepository donationRepository;

	private final MemberService memberService;

	@Override
	public ResponseEntity<DonationResponse> donate(DonationRequest donationRequest) {
		Member donorMember = memberService.findByLoginId(donationRequest.getDonorMemberLoginId());

		Member recipientMember = memberService.findByLoginId(
				donationRequest.getRecipientMemberLoginId());

		Donation donation = Donation.builder()
				.donor(donorMember)
				.recipient(recipientMember)
				.startCandyAmount(donationRequest.getStarCandyAmount())
				.message(donationRequest.getMessage())
				.build();

		donationRepository.save(donation);

		return ResponseEntity.ok().body(DonationResponse.success());
	}
}
