package ib.project.service.impl;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ib.project.model.Authority;
import ib.project.model.User;
import ib.project.repository.AuthorityRepository;
import ib.project.repository.UserRepository;
import ib.project.service.KeyStoreService;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	protected final Log LOGGER = LogFactory.getLog(getClass());

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private AuthorityRepository authorityRepository;

	@Autowired
	private KeyStoreService keyStoreService;

	// FUnkcija koja na osnovu username-a iz baze vraca objekat User-a
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
		} else {
			return user;
		}
	}

	// Funkcija pomocu koje korisnik menja svoju lozinku
	public void changePassword(String oldPassword, String newPassword) {

		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		String username = currentUser.getName();

		if (authenticationManager != null) {
			LOGGER.debug("Re-authenticating user '" + username + "' for password change request.");

			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
		} else {
			LOGGER.debug("No authentication manager set. can't change Password!");

			return;
		}

		LOGGER.debug("Changing password for user '" + username + "'");

		User user = (User) loadUserByUsername(username);

		// pre nego sto u bazu upisemo novu lozinku, potrebno ju je hesirati
		// ne zelimo da u bazi cuvamo lozinke u plain text formatu
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);

	}

	public void registerNewUser(String username, String password, String email) throws ParseException {
		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));

		List<Authority> allAuthorities = authorityRepository.findAll();
		Optional<Authority> result = allAuthorities.stream().filter(a -> a.getName().equals("ROLE_USER")).findFirst();
		user.setAuthorities(Collections.singletonList(result.get()));

		String location = keyStoreService.generateKeyStoreFile(username);
		user.setCertificate(location);

		userRepository.save(user);
	}
}
