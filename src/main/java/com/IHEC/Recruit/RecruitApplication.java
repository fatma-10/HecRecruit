package com.IHEC.Recruit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Point d'entrée de l'application HecRecruit.
 *
 * <p>Spring Security est inclus uniquement pour BCrypt ;
 * toute la configuration de sécurité des routes est désactivée via
 * {@code exclude = SecurityAutoConfiguration.class}.
 * L'authentification reste gérée manuellement via la session HTTP.</p>
 */
@SpringBootApplication
public class RecruitApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecruitApplication.class, args);
    }

    /**
     * Expose un {@link BCryptPasswordEncoder} comme bean Spring partagé.
     * Injecté dans {@code AuthService} pour l'encodage et la vérification
     * des mots de passe.
     *
     * @return une instance de BCryptPasswordEncoder (force = 10 par défaut)
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}