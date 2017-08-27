package hu.psprog.leaflet.lms.web.config;

import hu.psprog.leaflet.lms.web.auth.TokenRevokeLogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Spring Security configuration.
 *
 * @author Peter Smith
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String PATH_LOGIN = "/login";
    private static final String PATH_LOGOUT = "/logout";
    private static final String PATH_RECLAIM = "/password-reset/**";
    private static final String USERNAME_PARAMETER = "email";

    @Autowired
    private TokenRevokeLogoutHandler tokenRevokeLogoutHandler;

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
            .authorizeRequests()
                .antMatchers(PATH_LOGIN, PATH_RECLAIM)
                    .permitAll()
                .anyRequest()
                    .authenticated()
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
