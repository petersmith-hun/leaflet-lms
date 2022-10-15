package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.bridge.client.domain.error.ValidationErrorMessageListResponse;
import hu.psprog.leaflet.bridge.client.domain.error.ValidationErrorMessageResponse;
import hu.psprog.leaflet.lms.web.factory.ModelAndViewFactory;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static hu.psprog.leaflet.lms.web.controller.BaseController.FLASH_MESSAGE;
import static hu.psprog.leaflet.lms.web.interceptor.GeneralStatusSetterInterceptor.VALIDATION_FAILED_ATTRIBUTE;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Base test for controllers.
 *
 * @author Peter Smith
 */
public abstract class AbstractControllerTest {

    static final Long USER_ID = 5L;
    static final Optional<Integer> PAGE = Optional.of(1);
    static final Optional<Integer> LIMIT = Optional.of(10);

    static final String VIEW_LIST = "list";
    static final String VIEW_DETAILS = "details";
    static final String VIEW_EDIT_FORM = "edit_form";
    static final String VIEW_DELETE_FORM = "delete_form";

    static final String FIELD_CONTENT = "content";
    static final String FIELD_PAGINATION = "pagination";
    static final String FIELD_SEO = "seo";

    private static final String VIEW_NAME_FORMAT = "view/%s/%s";
    private static final String INVALID_FIELD_NAME = "field-1";
    private static final String INVALID_FIELD_VIOLATION = "field restriction violated";
    private static final ValidationErrorMessageListResponse VALIDATION_ERROR_MESSAGE_LIST_RESPONSE = ValidationErrorMessageListResponse.getBuilder()
            .withValidation(Collections.singletonList(ValidationErrorMessageResponse.getExtendedBuilder()
                    .withField(INVALID_FIELD_NAME)
                    .withMessage(INVALID_FIELD_VIOLATION)
                    .build()))
            .build();
    private static final Map<String, String> VALIDATION_RESULTS_MAP = new HashMap<>();
    private static final String SUBMITTED_FORM_VALUES = "submittedFormValues";
    private static final String VALIDATION_FAILED_MESSAGE = "Validation failed - please see violations";
    private static final String VALIDATION_RESULTS_ATTRIBUTE = "validationResults";

    static {
        VALIDATION_RESULTS_MAP.put(INVALID_FIELD_NAME, INVALID_FIELD_VIOLATION);
    }

    @Mock(lenient = true)
    HttpServletRequest request;

    @Mock(lenient = true)
    RedirectAttributes redirectAttributes;

    @Mock(lenient = true)
    Response response;

    @Mock(lenient = true)
    ModelAndViewFactory modelAndViewFactory;

    @Mock(lenient = true)
    ModelAndViewFactory.ModelAndViewWrapper modelAndViewWrapper;

    @Mock(lenient = true)
    ModelAndView modelAndView;


    @BeforeEach
    public void setup() {
        given(modelAndViewFactory.createForView(anyString())).willReturn(modelAndViewWrapper);
        given(modelAndViewFactory.createRedirectionTo(anyString())).willReturn(modelAndView);
        given(modelAndViewWrapper.withAttribute(anyString(), nullable(Object.class))).willReturn(modelAndViewWrapper);
        given(modelAndViewWrapper.build()).willReturn(modelAndView);
        given(response.readEntity(ValidationErrorMessageListResponse.class)).willReturn(VALIDATION_ERROR_MESSAGE_LIST_RESPONSE);
    }

    void verifyViewCreated(String name) {
        verify(modelAndViewFactory).createForView(String.format(VIEW_NAME_FORMAT, controllerViewGroup(), name));
    }

    void verifyRedirectionCreated(String to) {
        verify(modelAndViewFactory).createRedirectionTo(to);
    }

    void verifyFieldsSet(String... fieldName) {
        Arrays.stream(fieldName)
                .forEach(field -> verify(modelAndViewWrapper).withAttribute(eq(field), nullable(Object.class)));
    }

    void verifyFlashMessageSet() {
        verify(redirectAttributes).addFlashAttribute(eq(FLASH_MESSAGE), anyString());
    }

    void verifyStatusFlashMessage(boolean enabled) {
        verify(redirectAttributes).addFlashAttribute(eq(FLASH_MESSAGE), contains(enabled ? "enabled" : "disabled"));
    }

    void verifyDeletionFlashMessage(boolean permanent) {
        verify(redirectAttributes).addFlashAttribute(eq(FLASH_MESSAGE), contains(permanent ? "permanently" : "logically"));
    }

    void verifyUserLoggedOut() throws ServletException {
        verify(request).logout();
    }

    void verifyValidationViolationInfoSet(Object formContent) {
        verify(redirectAttributes).addFlashAttribute(FLASH_MESSAGE, VALIDATION_FAILED_MESSAGE);
        verify(redirectAttributes).addFlashAttribute(SUBMITTED_FORM_VALUES, formContent);
        verify(redirectAttributes).addFlashAttribute(VALIDATION_RESULTS_ATTRIBUTE, VALIDATION_RESULTS_MAP);
        verify(redirectAttributes).addFlashAttribute(VALIDATION_FAILED_ATTRIBUTE, true);
    }

    abstract String controllerViewGroup();
}
