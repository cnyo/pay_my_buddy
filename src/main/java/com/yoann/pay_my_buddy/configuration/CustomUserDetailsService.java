package com.yoann.pay_my_buddy.configuration;

import com.yoann.pay_my_buddy.controllers.TransactionController;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Custom implementation of {@link UserDetailsService} used by Spring Security to authenticate users
 * based on their email and assign them a default role.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final String DEFAULT_ROLE = "USER";
    private final Logger log = LogManager.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads a user by their email address. If the user is not found, throws a {@link UsernameNotFoundException}.
     *
     * @param email the email of the user to load
     * @return a {@link UserDetails} instance containing email, password, and authorities
     * @throws UsernameNotFoundException if no user is found with the provided email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Load user by email");
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.error("User not found");
            throw new UsernameNotFoundException("User not found");
        }
        log.debug("User found");
        return new org.springframework.security.core.userdetails.User(user.get().getEmail(), user.get().getPassword(), getGrantedAuthorities(DEFAULT_ROLE));
    }

    /**
     * Assigns a single authority role to the user.
     *
     * @param role the name of the role (e.g., "USER", "ADMIN")
     * @return a list containing a single {@link GrantedAuthority}
     */
    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

        return authorities;
    }
}
