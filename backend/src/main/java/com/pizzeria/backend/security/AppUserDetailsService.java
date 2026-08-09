package com.pizzeria.backend.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pizzeria.backend.entity.AppUser;
import com.pizzeria.backend.repository.AppUserRepository;

@Service
public class AppUserDetailsService implements UserDetailsService{

	private final AppUserRepository appUserRepository;
	
	public AppUserDetailsService(AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		AppUser appUser = appUserRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " +email));
		return new AppUserDetails(appUser);
	}

}
