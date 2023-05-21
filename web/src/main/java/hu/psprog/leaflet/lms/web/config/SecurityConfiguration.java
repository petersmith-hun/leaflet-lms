package hu.psprog.leaflet.lms.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration.
 *
 * @author Peter Smith
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String PATH_LOGOUT = "/logout";
    private static final String PATH_LOGIN_FAILURE = "/login?auth=fail";
    private static final String PATH_ACTUATOR = "/actuator/**";
    private static final String DEFAULT_SUCCESS_URL = "/";
    private static final String MINIMUM_REQUIRED_AUTHORITY = "SCOPE_read:entries";

    public static final String PATH_LOGIN = "/login";

    private final WebAppResources webAppResources;

    @Autowired
    public SecurityConfiguration(WebAppResources webAppResources) {
        this.webAppResources = webAppResources;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return web -> webAppResources.getResources().stream()
                .map(WebAppResources.WebAppResource::getHandler)
                .forEach(web.ignoring()::requestMatchers);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers(PATH_ACTUATOR)
                            .permitAll()
                        .anyRequest()
                            .hasAuthority(MINIMUM_REQUIRED_AUTHORITY))

                .oauth2Login(oauth2Login -> oauth2Login
                        .defaultSuccessUrl(DEFAULT_SUCCESS_URL)
                        .failureUrl(PATH_LOGIN_FAILURE))

                .logout(logout -> logout
                        .logoutUrl(PATH_LOGOUT)
                        .logoutSuccessUrl(PATH_LOGIN))

                .build();
    }
}
