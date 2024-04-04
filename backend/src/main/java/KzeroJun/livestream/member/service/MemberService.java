package KzeroJun.livestream.member.service;

import KzeroJun.livestream.member.entity.Member;

public interface MemberService {

	Member findByLoginId(String loginId);

}
