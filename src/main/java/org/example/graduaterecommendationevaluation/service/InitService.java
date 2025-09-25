package org.example.graduaterecommendationevaluation.service;

import lombok.RequiredArgsConstructor;
import org.example.graduaterecommendationevaluation.dox.User;
import org.example.graduaterecommendationevaluation.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InitService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        String name = "admin";
        if(userRepository.count() > 0) {
            return;
        }
        User admin = User.builder()
                .account(name)
                .password(passwordEncoder.encode(name))
                .name(name)
                .role(User.ADMIN)
                .build();
        userRepository.save(admin);
    }
}
