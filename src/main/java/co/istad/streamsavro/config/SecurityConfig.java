package co.istad.streamsavro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain configureSecurity(HttpSecurity http) throws Exception {

        // Config endpoints
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/login", "/ws/**").permitAll()
                .anyRequest().authenticated()
        );

        // Config form login
        http.formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("user_username")
                .passwordParameter("user_password")
        );

        return http.build();
    }

}
