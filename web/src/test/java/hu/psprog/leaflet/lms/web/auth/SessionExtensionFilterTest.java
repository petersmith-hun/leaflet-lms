package hu.psprog.leaflet.lms.web.auth;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.UserFacade;
import hu.psprog.leaflet.lms.web.auth.mock.WithMockedJWTUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static hu.psprog.leaflet.lms.web.auth.mock.MockedJWTUserSecurityContextFactory.EMAIL_ADDRESS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Unit tests for {@link SessionExtensionFilter}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(listeners = {
        DirtiesContextTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class})
public class SessionExtensionFilterTest {

    private static final int NOT_SOON_EXPIRATION_IN_MINUTES = 30;
    private static final int SOON_EXPIRATION_IN_MINUTES = 90;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserFacade userFacade;

    @InjectMocks
    private SessionExtensionFilter sessionExtensionFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void shouldNotTryToExtendSessionIfDisabled() throws ServletException, IOException {

        // given
        sessionExtensionFilter.setEnabled(false);

        // when
        sessionExtensionFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verifyZeroInteractions(userFacade);
    }

    @Test
    @WithMockedJWTUser
    public void shouldNotTryToExtendSessionIfTokenIsNotExpiringSoon() throws ServletException, IOException {

        // given
        sessionExtensionFilter.setEnabled(true);
        sessionExtensionFilter.setThreshold(NOT_SOON_EXPIRATION_IN_MINUTES);

        // when
        sessionExtensionFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verifyZeroInteractions(userFacade);
    }

    @Test
    @WithMockUser
    public void shouldNotTryToExtendSessionIfUserIsNotAuthenticatedByJWTToken() throws ServletException, IOException {

        // given
        sessionExtensionFilter.setEnabled(true);

        // when
        sessionExtensionFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verifyZeroInteractions(userFacade);
    }

    @Test
    @WithMockedJWTUser
    public void shouldExtendSessionIfTokenIsExpiringSoon() throws IOException, ServletException, CommunicationFailureException {

        // given
        sessionExtensionFilter.setEnabled(true);
        sessionExtensionFilter.setThreshold(SOON_EXPIRATION_IN_MINUTES);

        // when
        sessionExtensionFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verify(userFacade).renewToken(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @WithMockedJWTUser
    public void shouldKeepCurrentAuthenticationOnCommunicationFailure() throws CommunicationFailureException, ServletException, IOException {

        // given
        sessionExtensionFilter.setEnabled(true);
        sessionExtensionFilter.setThreshold(SOON_EXPIRATION_IN_MINUTES);
        doThrow(CommunicationFailureException.class).when(userFacade).renewToken(any(Authentication.class));

        // when
        sessionExtensionFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verify(userFacade).renewToken(SecurityContextHolder.getContext().getAuthentication());
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal(), equalTo(EMAIL_ADDRESS));
    }
}