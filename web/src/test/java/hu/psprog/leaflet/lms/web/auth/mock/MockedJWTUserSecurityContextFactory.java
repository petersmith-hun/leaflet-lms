package hu.psprog.leaflet.lms.web.auth.mock;

import hu.psprog.leaflet.lms.service.auth.JWTTokenAuthentication;
import hu.psprog.leaflet.lms.service.auth.user.AuthenticationUserDetailsModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Calendar;
import java.util.Date;

/**
 * Factory to create JWT authenticated based security context mock.
 *
 * @author Peter Smith
 */
public class MockedJWTUserSecurityContextFactory implements WithSecurityContextFactory<WithMockedJWTUser> {

    public static final String TOKEN = "token-for-mock-user";
    public static final String EMAIL_ADDRESS = "user@local.dev";
    private static final String USERNAME = "user1234";

    @Override
    public SecurityContext createSecurityContext(WithMockedJWTUser withMockedJWTUser) {

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new JWTTokenAuthentication.Builder()
                .withEmailAddress(EMAIL_ADDRESS)
                .withToken(TOKEN)
                .withDetails(prepareUserDetails(withMockedJWTUser))
                .withAuthenticated(withMockedJWTUser.authenticated())
                .build();
        securityContext.setAuthentication(authentication);

        return securityContext;
    }

    private AuthenticationUserDetailsModel prepareUserDetails(WithMockedJWTUser withMockedJWTUser) {

        AuthenticationUserDetailsModel userDetailsModel = new AuthenticationUserDetailsModel();
        userDetailsModel.setExpiration(createExpirationDate());
        userDetailsModel.setId(withMockedJWTUser.userID());
        userDetailsModel.setRole(withMockedJWTUser.role());
        userDetailsModel.setName(USERNAME);

        return userDetailsModel;
    }

    private Date createExpirationDate() {

        Calendar calendar = new Calendar.Builder()
                .setInstant(new Date())
                .build();
        calendar.add(Calendar.HOUR, 1);

        return calendar.getTime();
    }
}
