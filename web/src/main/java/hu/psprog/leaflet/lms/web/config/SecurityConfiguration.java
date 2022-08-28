package hu.psprog.leaflet.lms.web.config;

import hu.psprog.leaflet.rcp.hystrix.support.filter.HystrixContextFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

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
    private final HystrixContextFilter hystrixContextFilter;

    @Autowired
    public SecurityConfiguration(WebAppResources webAppResources, HystrixContextFilter hystrixContextFilter) {
        this.webAppResources = webAppResources;
        this.hystrixContextFilter = hystrixContextFilter;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return web -> webAppResources.getResources().stream()
                .map(WebAppResources.WebAppResource::getHandler)
                .forEach(web.ignoring()::antMatchers);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .addFilterBefore(hystrixContextFilter, LogoutFilter.class)

            .authorizeRequests()
                .antMatchers(PATH_ACTUATOR)
                    .permitAll()
                .anyRequest()
                    .hasAuthority(MINIMUM_REQUIRED_AUTHORITY)
                .and()

            .oauth2Login()
                .defaultSuccessUrl(DEFAULT_SUCCESS_URL)
                .failureUrl(PATH_LOGIN_FAILURE)
                .and()

            .logout()
                .logoutUrl(PATH_LOGOUT)
                .logoutSuccessUrl(PATH_LOGIN);

        return http.build();
    }
}
