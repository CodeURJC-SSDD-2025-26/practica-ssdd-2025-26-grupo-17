package es.dawgroup17.nexgym.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.dawgroup17.nexgym.model.AppUser;
import es.dawgroup17.nexgym.repository.UserRepository;

//This code is used like a bridge between the DB and the Spring secutiry

//Spring calls automatically this method when a user is doing Login
@Service
public class RepositoryUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Looks for the user by its email, if it doesn't exist throws an exception
        AppUser user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Converts the roles into a list of grantedAuthority
        List<GrantedAuthority> roles = new ArrayList<>();
        for (String role : user.getRoles()) {
            roles.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        // Returns the user with = email, encoded password and roles
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getEncodedPassword(),
                roles);
    }
}