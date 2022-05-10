package com.almeidinha.app;

import com.almeidinha.app.entities.Authority;
import com.almeidinha.app.entities.User;
import com.almeidinha.app.repository.UserDetailRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SpringSecurityDemoApplication {

	private final PasswordEncoder passwordEncoder;

	private final UserDetailRepository userDetailRepository;

	public SpringSecurityDemoApplication(PasswordEncoder passwordEncoder, UserDetailRepository userDetailRepository) {
		this.passwordEncoder = passwordEncoder;
		this.userDetailRepository = userDetailRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityDemoApplication.class, args);
	}

	@PostConstruct
	protected void init() {
		List<Authority> authorityList = new ArrayList<>();
		authorityList.add(new Authority("USER", "User role"));
		authorityList.add(new Authority("ADMIN", "Admin role"));

		User user = new User("mike", "marcos", "almeida");
		user.setAuthorities(authorityList);
		user.setPassword(this.passwordEncoder.encode("password"));
		user.setEnabled(true);

		this.userDetailRepository.save(user);
	}

}
