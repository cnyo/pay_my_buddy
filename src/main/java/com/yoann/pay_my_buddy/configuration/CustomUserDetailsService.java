package com.yoann.pay_my_buddy.configuration;

import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.repository.UserRepository;
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

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final String DEFAULT_ROLE = "USER";

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(name);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), getGrantedAuthorities(DEFAULT_ROLE));
    }

    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

        return authorities;
    }
}
