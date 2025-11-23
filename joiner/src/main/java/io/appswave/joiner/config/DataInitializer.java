package io.appswave.joiner.config;

import io.appswave.joiner.enums.UserRole;
import io.appswave.joiner.entity.User;
import io.appswave.joiner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Value("${security.admin.email}")
    String adminEmail;
    @Value("${security.admin.password}")
    String password;

    @Bean
    CommandLineRunner initDefaultAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setFirstName("Root");
                admin.setLastName("Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(password));
                admin.setUserRole(UserRole.ADMIN);

                userRepository.save(admin);
                System.out.println("Default admin account created: " + adminEmail);
            }
        };
    }
}

