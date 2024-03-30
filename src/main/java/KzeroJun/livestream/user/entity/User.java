package KzeroJun.livestream.user.entity;

import KzeroJun.livestream.user.constant.Role;
import KzeroJun.livestream.user.constant.Status;
import jakarta.persistence.Access;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;

	private String password;

	private String nickname;

	@CreatedDate
	private LocalDateTime createdAt;

	@Enumerated(value = EnumType.STRING)
	private Status status;

	@Enumerated(value = EnumType.STRING)
	private Role role;

	@Column(name = "star_candy_amount")
	private Integer starCandyAmount;

	@Column(name = "verification")
	private Boolean verification;

	@Builder
	public User(String email, String password, String nickname) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.status = Status.ACTIVATE;
		this.role = Role.USER;
		this.starCandyAmount = 0;
		this.verification = false;
	}

}