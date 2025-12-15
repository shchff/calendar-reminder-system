package ru.grigorii.calendar_reminder_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.List;

/**
 * Конфигурация приложения
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class GeneralConfig
{
    /**
     * Бин для OpenAPI
     */
    @Bean
    public OpenAPI calendarOpenAPI()
    {
        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/")
                                .description("Base API Path")
                ))
                .info(new Info()
                        .title("Calendar Reminder System API")
                        .description("REST API for Calendar Reminder System")
                        .version("1.0.0"));
    }

    /**
     * Бин цепочки фильтров для Spring Security
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception
    {
        return security
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/register", "/perform_login", "/css/**", "/js/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/**"
                        ).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/perform_login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/calendars", true)
                        .failureUrl("/auth/login?error=true")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .build();
    }

    /**
     * Бин шифратора паролей с реализацией через BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}