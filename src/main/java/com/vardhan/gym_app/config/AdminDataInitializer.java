package com.vardhan.gym_app.config;

import com.vardhan.gym_app.entity.AppUser;
import com.vardhan.gym_app.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.staff.username}")
    private String staffUsername;

    @Value("${app.staff.password}")
    private String staffPassword;

    @Override
    public void run(String... args) {
        createUserIfMissing(adminUsername, adminPassword, "Gym Administrator", "ADMIN");
        createUserIfMissing(staffUsername, staffPassword, "Front Desk Staff", "STAFF");
    }

    private void createUserIfMissing(String username, String rawPassword, String fullName, String role) {
        if (appUserRepository.existsByUsernameIgnoreCase(username)) {
            return;
        }

        AppUser user = AppUser.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .fullName(fullName)
                .role(role)
                .enabled(true)
                .build();

        appUserRepository.save(user);
        log.info("User '{}' created with role {}", username, role);
    }
}