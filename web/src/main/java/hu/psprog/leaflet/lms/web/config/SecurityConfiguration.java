package hu.psprog.leaflet.lms.web.config;

import hu.psprog.leaflet.jwt.auth.support.filter.SessionExtensionFilter;
import hu.psprog.leaflet.jwt.auth.support.logout.TokenRevokeLogoutHandler;
import hu.psprog.leaflet.rcp.hystrix.support.filter.HystrixContextFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * Spring Security configuration.
 *
 * @author Peter Smith
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String PATH_LOGOUT = "/logout";
    private static final String PATH_RECLAIM = "/password-reset/**";
    private static final String PATH_LOGIN_FAILURE = "/login?auth=fail";
    private static final String PATH_ACTUATOR = "/actuator/**";
    private static final String DEFAULT_SUCCESS_URL = "/";
    private static final String USERNAME_PARAMETER = "email";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_EDITOR = "EDITOR";

    public static final String PATH_LOGIN = "/login";

    private final TokenRevokeLogoutHandler tokenRevokeLogoutHandler;
    private final SessionExtensionFilter sessionExtensionFilter;
    private final WebAppResources webAppResources;
    private final HystrixContextFilter hystrixContextFilter;

    @Autowired
    public SecurityConfiguration(SessionExtensionFilter sessionExtensionFilter, TokenRevokeLogoutHandler tokenRevokeLogoutHandler,
                                 WebAppResources webAppResources, HystrixContextFilter hystrixContextFilter) {
        this.sessionExtensionFilter = sessionExtensionFilter;
        this.tokenRevokeLogoutHandler = tokenRevokeLogoutHandler;
        this.webAppResources = webAppResources;
        this.hystrixContextFilter = hystrixContextFilter;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        webAppResources.getResources().stream()
                .map(WebAppResources.WebAppResource::getHandler)
                .forEach(web.ignoring()::antMatchers);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .addFilterBefore(hystrixContextFilter, LogoutFilter.class)
            .addFilterAfter(sessionExtensionFilter, UsernamePasswordAuthenticationFilter.class)

            .authorizeRequests()
                .antMatchers(PATH_LOGIN, PATH_RECLAIM, PATH_ACTUATOR)
                    .permitAll()
                .anyRequest()
                    .hasAnyAuthority(ROLE_ADMIN, ROLE_EDITOR)
                .and()

            .formLogin()
                .loginPage(PATH_LOGIN)
                .failureUrl(PATH_LOGIN_FAILURE)
                .usernameParameter(USERNAME_PARAMETER)
                .defaultSuccessUrl(DEFAULT_SUCCESS_URL, true)
                .and()

            .logout()
                .logoutUrl(PATH_LOGOUT)
                .logoutSuccessUrl(PATH_LOGIN)
                .addLogoutHandler(tokenRevokeLogoutHandler);
    }
}
