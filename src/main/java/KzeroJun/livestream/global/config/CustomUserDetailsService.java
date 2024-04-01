package KzeroJun.livestream.global.config;

import KzeroJun.livestream.auth.exception.LoginFailedException;
import KzeroJun.livestream.member.entity.Member;
import KzeroJun.livestream.member.repository.UserRepository;
import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByEmail(username)
				.map(this::createUserDetails)
				.orElseThrow(LoginFailedException::new);
	}

	private UserDetails createUserDetails(Member member) {
		Collection<? extends GrantedAuthority> authorities = getAuthorities(member);
		return new User(member.getEmail(), member.getPassword(), authorities);
	}

	private Collection<? extends GrantedAuthority> getAuthorities(Member member) {
		return Collections.singletonList(
				new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));
	}
}
