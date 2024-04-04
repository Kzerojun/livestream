package KzeroJun.livestream.member.service.implement;

import KzeroJun.livestream.member.entity.Member;
import KzeroJun.livestream.member.exception.NoExistedMember;
import KzeroJun.livestream.member.repository.MemberRepository;
import KzeroJun.livestream.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;

	@Override
	public Member findByLoginId(String loginId) {
		return memberRepository.findByLoginId(loginId).orElseThrow(NoExistedMember::new);
	}
}
