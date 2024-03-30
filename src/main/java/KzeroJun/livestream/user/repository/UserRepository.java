package KzeroJun.livestream.user.repository;

import KzeroJun.livestream.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);
}
