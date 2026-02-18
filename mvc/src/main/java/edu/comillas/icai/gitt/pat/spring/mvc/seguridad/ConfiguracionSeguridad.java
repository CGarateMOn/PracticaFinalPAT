package edu.comillas.icai.gitt.pat.spring.mvc.seguridad;

import edu.comillas.icai.gitt.pat.spring.mvc.data.AlmacenDatos;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Usuario;
import jakarta.servlet.http.HttpServletResponse;
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
                .csrf(csrf -> csrf.ignoringRequestMatchers("/pistaPadel/**", "/reservations/**"))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/pistaPadel/auth/register").permitAll()
                        .requestMatchers("/pistaPadel/auth/login", "/pistaPadel/auth/me").authenticated()

                        // -------- PUBLICOS --------
                        .requestMatchers("/pistaPadel/courts").permitAll()
                        .requestMatchers("/pistaPadel/courts/*").permitAll()
                        .requestMatchers("/pistaPadel/availability").permitAll()
                        .requestMatchers("/pistaPadel/courts/*/availability").permitAll()
                        
                        .anyRequest().authenticated()
                )
//
                // LOGOUT CONFIGURACION
                .logout(logout -> logout
                        .logoutUrl("/pistaPadel/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 ok
                        })
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
    @Bean
    public UserDetailsService usuarios() {
        return email -> {
            Usuario u = AlmacenDatos.usuarios.values().stream()
                .filter(user -> user.email().equals(email))
                .findFirst()
                .orElseThrow( ()-> new org.springframework.security.core.userdetails.UsernameNotFoundException("No existe"));
        return User.withDefaultPasswordEncoder()
                .username(u.email())
                .password(u.password())
                .roles(u.rol().nombreRol())
                .build();
        };
    }
}

