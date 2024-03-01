package com.optimised.security;

import com.optimised.model.User;
import com.optimised.repository.UserRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuthenticatedUser {
    private static final Logger log = LogManager.getLogger(AuthenticatedUser.class);
    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(AuthenticationContext authenticationContext, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    @Transactional
    public Optional<User> get() {
        Optional<User> user = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(userDetails -> userRepository.findByUsername(userDetails.getUsername()));
        if (user.isPresent()){
            log.info("User: " + user.get().getUsername() + " logged in");
        }
        return user;
    }

    public void logout() {
        log.info("User: " + authenticationContext.getPrincipalName().get() + " logged out");
        authenticationContext.logout();
    }

}
