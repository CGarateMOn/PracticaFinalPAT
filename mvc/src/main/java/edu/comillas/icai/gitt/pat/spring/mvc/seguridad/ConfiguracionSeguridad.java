package edu.comillas.icai.gitt.pat.spring.mvc.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ConfiguracionSeguridad {
    @Bean
    public SecurityFilterChain configuracion(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/pistaPadel/**")) // MUY IMPORTANTE: debe coincidir con tu ruta
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/pistaPadel/availability").permitAll()
                        .requestMatchers("/pistaPadel/courts/**/availability").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()); // Esto es lo que usa Postman

        return http.build();
    }
    @Bean
    public UserDetailsService usuarios() {
        // Usamos .builder() directamente para tener control total
        UserDetails user = User.builder()
                .username("user01")
                .password("{noop}cifrado456") // El prefijo {noop} le dice a Spring: "no cifres esto"
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin01")
                .password("{noop}cifrado123")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }
}

