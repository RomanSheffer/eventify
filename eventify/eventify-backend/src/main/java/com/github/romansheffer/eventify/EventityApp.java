package com.github.romansheffer.eventify;


import com.github.romansheffer.eventify.model.User;
import com.github.romansheffer.eventify.repository.UserRepository;
import com.github.romansheffer.eventify.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@SpringBootApplication
public class EventityApp {

    private final NotificationService notificationService;


    public static void main(String[] args) {
        SpringApplication.run(EventityApp.class);

    }


    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {


            // встраиваем администратора
            if (!userRepository.existsByEmail("admin@admin.com")) {

                User admin = new User();

                admin.setEmail("admin@admin.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ADMIN");

                userRepository.save(admin);
                notificationService.createNotifications(admin);


            }
        };
    }


}
