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
    private static final String USERNAME_PARAMETER = "email";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_EDITOR = "EDITOR";

    public static final String PATH_LOGIN = "/login";

    @Autowired
    private TokenRevokeLogoutHandler tokenRevokeLogoutHandler;

    @Autowired
    private SessionExtensionFilter sessionExtensionFilter;

    @Override
    public void configure(WebSecurity web) throws Exception {

        web
            .ignoring()
                .antMatchers("/css/**")
                .antMatchers( "/fonts/**")
                .antMatchers("/scripts/**");
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
                .and()

            .logout()
                .logoutUrl(PATH_LOGOUT)
                .logoutSuccessUrl(PATH_LOGIN)
                .addLogoutHandler(tokenRevokeLogoutHandler);
    }
}
