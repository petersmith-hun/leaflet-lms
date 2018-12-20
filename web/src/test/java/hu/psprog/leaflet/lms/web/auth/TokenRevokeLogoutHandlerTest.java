package hu.psprog.leaflet.lms.web.auth;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.UserFacade;
import hu.psprog.leaflet.lms.web.exception.CouldNotReachBackendException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link TokenRevokeLogoutHandler}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class TokenRevokeLogoutHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private UserFacade userFacade;

    @InjectMocks
    private TokenRevokeLogoutHandler tokenRevokeLogoutHandler;

    @Test
    public void shouldCallRevokeTokenEndpoint() throws CommunicationFailureException {

        // when
        tokenRevokeLogoutHandler.logout(request, response, authentication);

        // then
        verify(userFacade).revokeToken();
    }

    @Test(expected = CouldNotReachBackendException.class)
    public void shouldThrowCouldNotReachBackendExceptionOnCommunicationFailure() throws CommunicationFailureException {

        // given
        doThrow(CommunicationFailureException.class).when(userFacade).revokeToken();

        // when
        tokenRevokeLogoutHandler.logout(request, response, authentication);

        // then
        // exception expected
    }
}