/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package dsp.templateEngine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dsp.templateEngine.dto.UserDetailsDTO;
import dsp.templateEngine.entity.BaseUser;
import dsp.templateEngine.repository.MyUserRepository;

/**
 * 
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private MyUserRepository myUserRepository;

//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder; // Ensure this bean is available

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		BaseUser baseUser = myUserRepository.findByUsername(username);
		if (baseUser == null) {
			throw new UsernameNotFoundException("User not found");
		}

		// Create a UserDetailsDTO object from BaseUser
		return new UserDetailsDTO(baseUser.getId(), baseUser.getUsername(), baseUser.getPhone(), baseUser.getEmail(),
				baseUser.getPassword(), baseUser.getRole(), baseUser.getIsActive());
	}

}