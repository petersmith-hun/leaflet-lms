package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.bridge.client.domain.error.ErrorMessageResponse;
import hu.psprog.leaflet.bridge.client.domain.error.ValidationErrorMessageListResponse;
import hu.psprog.leaflet.bridge.client.domain.error.ValidationErrorMessageResponse;
import hu.psprog.leaflet.bridge.client.exception.DefaultNonSuccessfulResponseException;
import hu.psprog.leaflet.bridge.client.exception.ResourceNotFoundException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.ws.rs.core.Response;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link BaseController}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class BaseControllerTest {

    private static final String ERROR_MESSAGE = "Error occurred";
    private static final String ATTRIBUTE_MESSAGE = "message";
    private static final String ATTRIBUTE_VALIDATION_ERRORS = "validationErrors";
    private static final String VALIDATION_FAILURE = "Validation failure.";
    private static final String VALIDATION_ERROR_PAGE = "view/error/validation";
    private static final String DEFAULT_HANDLER = "Default handler";
    private static final ValidationErrorMessageResponse INVALID_VALUE = ValidationErrorMessageResponse.getExtendedBuilder()
            .withField("field-1")
            .withMessage("Invalid value")
            .build();

    @Mock
    private Response response;

    @InjectMocks
    private BaseController baseController;

    @Test
    public void shouldHandleDefaultNonSuccessfulResponseException() {

        // given
        prepareErrorMessage();
        DefaultNonSuccessfulResponseException exception = new ResourceNotFoundException(response);

        // when
        ModelAndView result = baseController.bridgeNonSuccessfulResponseExceptionHandler(exception);

        // then
        assertThat(result.getViewName(), equalTo(BaseController.DEFAULT_ERROR_PAGE));
        assertThat(result.getModel().get(ATTRIBUTE_MESSAGE), equalTo(ERROR_MESSAGE));
        assertThat(result.getStatus(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    public void shouldHandleValidationFailureException() {

        // given
        prepareValidationErrorMessage();
        ValidationFailureException exception = new ValidationFailureException(response);

        // when
        ModelAndView result = baseController.bridgeValidationFailureExceptionHandler(exception);

        // then
        assertThat(result.getViewName(), equalTo(VALIDATION_ERROR_PAGE));
        assertThat(result.getModel().get(ATTRIBUTE_MESSAGE), equalTo(VALIDATION_FAILURE));
        assertThat(result.getModel().get(ATTRIBUTE_VALIDATION_ERRORS), equalTo(Collections.singletonList(INVALID_VALUE)));
        assertThat(result.getStatus(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void shouldHandleAnyOtherException() {

        // given
        IllegalStateException exception = new IllegalStateException(DEFAULT_HANDLER);

        // when
        ModelAndView result = baseController.defaultExceptionHandler(exception);

        // then
        assertThat(result.getViewName(), equalTo(BaseController.DEFAULT_ERROR_PAGE));
        assertThat(result.getModel().get(ATTRIBUTE_MESSAGE), equalTo(DEFAULT_HANDLER));
        assertThat(result.getStatus(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private void prepareErrorMessage() {
        given(response.getStatus()).willReturn(404);
        given(response.readEntity(ErrorMessageResponse.class)).willReturn(ErrorMessageResponse.getBuilder()
                .withMessage(ERROR_MESSAGE)
                .build());
    }

    private void prepareValidationErrorMessage() {
        given(response.readEntity(ValidationErrorMessageListResponse.class)).willReturn(ValidationErrorMessageListResponse.getBuilder()
                .withValidation(Collections.singletonList(INVALID_VALUE))
                .build());
    }
}