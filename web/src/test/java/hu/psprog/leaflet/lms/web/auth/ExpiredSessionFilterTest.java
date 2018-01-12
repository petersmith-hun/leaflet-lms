package hu.psprog.leaflet.lms.web.auth;

import hu.psprog.leaflet.lms.service.auth.JWTTokenAuthentication;
import hu.psprog.leaflet.lms.web.auth.mock.WithMockedJWTUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Unit tests for {@link ExpiredSessionFilter}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(listeners = {
        DirtiesContextTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class})
public class ExpiredSessionFilterTest {

    private static final String COOKIE_NAME = "cookie-name";
    private static final String COOKIE_VALUE = "cookie-value";

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private ExpiredSessionFilter expiredSessionFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
        request.setSession(session);
        request.setCookies(new Cookie(COOKIE_NAME, COOKIE_VALUE));
    }

    @Test
    @WithMockedJWTUser
    public void shouldClearSessionOnUnauthorizedResponse() throws ServletException, IOException {

        //given
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // when
        expiredSessionFilter.doFilterInternal(request, response, filterChain);

        // then
        assertInvalidatedSession();
    }

    @Test
    @WithMockedJWTUser
    public void shouldNotClearSessionOnHTTP200Response() throws ServletException, IOException {

        //given
        response.setStatus(HttpServletResponse.SC_OK);

        // when
        expiredSessionFilter.doFilterInternal(request, response, filterChain);

        // then
        assertValidSession(true);
    }

    @Test
    @WithMockUser
    public void shouldNotClearSessionWhenUserIsNotAuthenticatedByJWT() throws ServletException, IOException {

        //given
        response.setStatus(HttpServletResponse.SC_OK);

        // when
        expiredSessionFilter.doFilterInternal(request, response, filterChain);

        // then
        assertValidSession(false);
    }

    @Test
    @WithMockedJWTUser(authenticated = false)
    public void shouldNotClearSessionWhenUserIsMarkedAsNotAuthenticated() throws ServletException, IOException {

        //given
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // when
        expiredSessionFilter.doFilterInternal(request, response, filterChain);

        // then
        assertValidSession(true);
    }

    private void assertValidSession(boolean withJWTAuthentication) {
        assertThat(SecurityContextHolder.getContext().getAuthentication(), notNullValue());
        assertThat(SecurityContextHolder.getContext().getAuthentication() instanceof JWTTokenAuthentication, is(withJWTAuthentication));
        assertThat(session.isInvalid(), is(false));
        assertThat(Arrays.stream(request.getCookies()).noneMatch(cookie -> cookie.getMaxAge() == 0), is(true));
    }

    private void assertInvalidatedSession() {
        assertThat(SecurityContextHolder.getContext().getAuthentication(), nullValue());
        assertThat(session.isInvalid(), is(true));
        assertThat(Arrays.stream(request.getCookies()).allMatch(cookie -> cookie.getMaxAge() == 0), is(true));
    }
}