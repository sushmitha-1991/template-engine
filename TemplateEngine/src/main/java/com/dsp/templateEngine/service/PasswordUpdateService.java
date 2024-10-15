/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dsp.templateEngine.entity.BaseUser;
import com.dsp.templateEngine.repository.MyUserRepository;

import jakarta.annotation.PostConstruct;

/**
 * 
 */
@Service
public class PasswordUpdateService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MyUserRepository userRepository;

	@PostConstruct
	public void updatePasswords() {
		List<BaseUser> users = userRepository.findAll();
		for (BaseUser user : users) {
			if (!isPasswordEncoded(user.getPassword())) {
				// Encode the plain text password
				String encodedPassword = passwordEncoder.encode(user.getPassword());
				user.setPassword(encodedPassword);
				userRepository.save(user);
			}
		}
	}

	private boolean isPasswordEncoded(String password) {
		// Implement the logic to check if the password is encoded
		// For example, check the length or format of the encoded password
		return password != null && (password.length() == 60 || password.startsWith("$2a$"));
		// Adjust the length or prefix based on your encoder's output format
	}
}
