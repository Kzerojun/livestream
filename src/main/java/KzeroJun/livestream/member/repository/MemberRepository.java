package KzeroJun.livestream.member.repository;

import KzeroJun.livestream.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsByLoginId(String loginId);

	boolean existsByNickname(String nickname);

	Optional<Member> findByLoginId(String loginId);
}
