package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.bridge.client.exception.UnauthorizedAccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link DefaultErrorController}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class DefaultErrorControllerTest {

    private static final String MESSAGE = "message";
    private static final String EXCEPTION = "exception";
    private static final String TRACE = "trace";
    private static final String STATUS = "status";
    private static final String ERROR = "error";

    @Mock
    private ErrorAttributes errorAttributes;

    @Mock
    private HttpServletRequest request;

    @Captor
    private ArgumentCaptor<ErrorAttributeOptions> errorAttributesArgumentCaptor;

    @InjectMocks
    private DefaultErrorController defaultErrorController;

    @Test
    public void shouldHandleAuthorizationError() {

        // given
        getErrorAttributesMock().willReturn(prepareAuthorizationError());

        // when
        ModelAndView result = defaultErrorController.defaultErrorHandler(request);

        // then
        assertThat(result.getViewName(), equalTo(BaseController.DEFAULT_ERROR_PAGE));
        assertThat(result.getStatus(), equalTo(HttpStatus.UNAUTHORIZED));
        assertThat(result.getModel().get(MESSAGE), equalTo("Authorization error: Unauthorized access"));
        assertErrorAttributeOptions();
    }

    @Test
    public void shouldHandleNotFoundException() {

        // given
        getErrorAttributesMock().willReturn(prepareNotFoundException());

        // when
        ModelAndView result = defaultErrorController.defaultErrorHandler(request);

        // then
        assertThat(result.getViewName(), equalTo(BaseController.DEFAULT_ERROR_PAGE));
        assertThat(result.getStatus(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(result.getModel().get(MESSAGE), equalTo("Not found: Resource not found"));
        assertErrorAttributeOptions();
    }

    @Test
    public void shouldHandleDefaultErrorWithTraceMessage() {

        // given
        getErrorAttributesMock().willReturn(prepareDefaultError());

        // when
        ModelAndView result = defaultErrorController.defaultErrorHandler(request);

        // then
        assertThat(result.getViewName(), equalTo(BaseController.DEFAULT_ERROR_PAGE));
        assertThat(result.getStatus(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(result.getModel().get(MESSAGE), equalTo("Exception caught: Something went wrong"));
        assertErrorAttributeOptions();
    }

    private Map<String, Object> prepareAuthorizationError() {

        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put(EXCEPTION, UnauthorizedAccessException.class.getName());
        errorMap.put(ERROR, "Authorization error");
        errorMap.put(MESSAGE, "Unauthorized access");

        return errorMap;
    }

    private Map<String, Object> prepareNotFoundException() {

        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put(STATUS, 404);
        errorMap.put(ERROR, "Not found");
        errorMap.put(MESSAGE, "Resource not found");

        return errorMap;
    }

    private Map<String, Object> prepareDefaultError() {

        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put(ERROR, "Exception caught");
        errorMap.put(MESSAGE, "Something went wrong");
        errorMap.put(TRACE, "This is the trace message");

        return errorMap;
    }

    private BDDMockito.BDDMyOngoingStubbing<Map<String, Object>> getErrorAttributesMock() {
        return given(errorAttributes.getErrorAttributes(any(WebRequest.class), errorAttributesArgumentCaptor.capture()));
    }

    private void assertErrorAttributeOptions() {
        assertThat(errorAttributesArgumentCaptor.getValue().getIncludes(), hasItems(ErrorAttributeOptions.Include.values()));
    }
}