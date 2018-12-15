package hu.psprog.leaflet.lms.web.config;

import hu.psprog.leaflet.lms.web.auth.SessionExtensionFilter;
import hu.psprog.leaflet.lms.web.auth.TokenRevokeLogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
    private static final String DEFAULT_SUCCESS_URL = "/";
    private static final String USERNAME_PARAMETER = "email";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_EDITOR = "EDITOR";

    public static final String PATH_LOGIN = "/login";

    private final TokenRevokeLogoutHandler tokenRevokeLogoutHandler;
    private final SessionExtensionFilter sessionExtensionFilter;
    private final WebAppResources webAppResources;

    @Autowired
    public SecurityConfiguration(SessionExtensionFilter sessionExtensionFilter, TokenRevokeLogoutHandler tokenRevokeLogoutHandler, WebAppResources webAppResources) {
        this.sessionExtensionFilter = sessionExtensionFilter;
        this.tokenRevokeLogoutHandler = tokenRevokeLogoutHandler;
        this.webAppResources = webAppResources;
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
            .addFilterAfter(sessionExtensionFilter, UsernamePasswordAuthenticationFilter.class)

            .authorizeRequests()
                .antMatchers(PATH_LOGIN, PATH_RECLAIM)
                    .permitAll()
                .anyRequest()
                    .hasAnyAuthority(ROLE_ADMIN, ROLE_EDITOR)
                .and()

            .formLogin()
                .loginPage(PATH_LOGIN)
                .failureForwardUrl(PATH_LOGIN)
                .usernameParameter(USERNAME_PARAMETER)
                .defaultSuccessUrl(DEFAULT_SUCCESS_URL, true)
                .and()

            .logout()
                .logoutUrl(PATH_LOGOUT)
                .logoutSuccessUrl(PATH_LOGIN)
                .addLogoutHandler(tokenRevokeLogoutHandler);
    }
}
