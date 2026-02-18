package edu.comillas.icai.gitt.pat.spring.mvc.seguridad;

import edu.comillas.icai.gitt.pat.spring.mvc.data.AlmacenDatos;
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

                        // -------- AUTH --------
                        .requestMatchers("/pistaPadel/auth/**").permitAll()

                        // -------- PUBLICOS --------
                        .requestMatchers("/pistaPadel/courts").permitAll()
                        .requestMatchers("/pistaPadel/courts/*").permitAll()
                        .requestMatchers("/pistaPadel/availability").permitAll()
                        .requestMatchers("/pistaPadel/courts/*/availability").permitAll()
                        
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/pistaPadel/auth/login")
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK); // 200 ok
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 credenciales incorrectas
                        })
                )
                // LOGOUT CONFIGURACION
                .logout(logout -> logout
                        .logoutUrl("/pistaPadel/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 ok
                        })
                )
                .httpBasic(Customizer.withDefaults()); // Esto es lo que usa Postman
//                        .requestMatchers("/auth/**").permitAll()
//                        .requestMatchers("/pistaPadel/availability", "/pistaPadel/courts").permitAll()
//                        .anyRequest().authenticated()
                

        return http.build();
    }
    @Bean
    public UserDetailsService usuarios() {
        return username -> {
        edu.comillas.icai.gitt.pat.spring.mvc.records.Usuario u = AlmacenDatos.usuarios.values().stream()
                .filter(user -> user.email().equals(username))
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

