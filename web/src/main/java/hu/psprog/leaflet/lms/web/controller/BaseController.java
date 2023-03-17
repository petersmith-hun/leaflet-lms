package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.bridge.client.domain.error.ValidationErrorMessageListResponse;
import hu.psprog.leaflet.bridge.client.domain.error.ValidationErrorMessageResponse;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.exception.DefaultNonSuccessfulResponseException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.lms.web.factory.ModelAndViewFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static hu.psprog.leaflet.lms.web.interceptor.GeneralStatusSetterInterceptor.VALIDATION_FAILED_ATTRIBUTE;

/**
 * Base controller.
 * Including exception handlers.
 *
 * @author Peter Smith
 */
@ControllerAdvice
class BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    private static final String VIEW_ATTRIBUTE_MESSAGE = "message";
    private static final String VIEW_ATTRIBUTE_VALIDATION_ERRORS = "validationErrors";
    private static final String VALIDATION_ERROR_PAGE = "view/error/validation";
    private static final String SUBMITTED_FORM_VALUES = "submittedFormValues";
    private static final String VALIDATION_FAILED_MESSAGE = "Validation failed - please see violations";
    private static final String VALIDATION_RESULTS_ATTRIBUTE = "validationResults";
    private static final String SUBJECT_JWT_ATTRIBUTE = "sub";

    static final String DEFAULT_ERROR_PAGE = "view/error/default";

    static final String PATH_CREATE = "/create";
    static final String PATH_VIEW = "/view/{id}";
    static final String PATH_EDIT = "/edit/{id}";
    static final String PATH_STATUS = "/status/{id}";
    static final String PATH_PUBLICATION = "/publication/{id}";
    static final String PATH_DELETE = "/delete/{id}";
    static final String PATH_OPTIONAL_PAGE_NUMBER = "/{page}";
    static final String PATH_CURRENT = "";

    static final String PATH_VARIABLE_ID = "id";

    static final String FLASH_MESSAGE = "flash";

    static final String REQUEST_PARAM_REDIRECT = "redirect";

    @Autowired
    ModelAndViewFactory modelAndViewFactory;

    /**
     * Returns current authenticated user's ID.
     *
     * @return user ID as {@link Long}
     */
    Long currentUserID() {

        Long userID = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2User principal = ((OAuth2AuthenticationToken) authentication).getPrincipal();
            userID = Optional.ofNullable(principal.getAttribute(SUBJECT_JWT_ATTRIBUTE))
                    .filter(subject -> subject instanceof String)
                    .map(subject -> Long.valueOf((String) subject))
                    .orElse(0L);
        }

        return userID;
    }

    /**
     * Provides generalized server-side validation failure handling.
     * If the request processing supplier call throws {@link ValidationFailureException}, then the failure redirection supplier will be called.
     * Both of the suppliers should return with a populated {@link ModelAndView} object - latter one preferably with a redirection.
     *
     * @param successfulModelAndViewSupplier {@link ModelAndView} supplier for successful processing as {@link BridgeCallSupplier}
     * @param failureRedirectionFunction {@link ModelAndView} function for validation failure handling
     * @return populated {@link ModelAndView}
     * @throws CommunicationFailureException on communication failure with the backend
     */
    ModelAndView handleValidationFailure(BridgeCallSupplier<ModelAndView> successfulModelAndViewSupplier,
                                         Function<Map<String, String>, ModelAndView> failureRedirectionFunction)
            throws CommunicationFailureException {

        ModelAndView modelAndView;
        try {
            modelAndView = successfulModelAndViewSupplier.get();
        } catch (ValidationFailureException e) {
            LOGGER.warn("Server side validation failure");
            modelAndView = failureRedirectionFunction.apply(extractValidationViolations(e.getErrorMessage()));
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }

        return modelAndView;
    }

    /**
     * Handles any type of exceptions thrown by Bridge (except for {@link ValidationFailureException}).
     *
     * @param exception the thrown exception
     * @return ModelAndView instance set to the error page view and including error message, and status
     */
    @ExceptionHandler(DefaultNonSuccessfulResponseException.class)
    public ModelAndView bridgeNonSuccessfulResponseExceptionHandler(DefaultNonSuccessfulResponseException exception) {
        LOGGER.error("Leaflet failed to process request.", exception);
        return buildModelAndView(exception);
    }

    /**
     * Handles {@link ValidationFailureException} exceptions thrown by Bridge.
     *
     * @param exception the thrown exception
     * @return ModelAndView instance set to the error page view and including error message, validation results, and status
     */
    @ExceptionHandler(ValidationFailureException.class)
    public ModelAndView bridgeValidationFailureExceptionHandler(ValidationFailureException exception) {
        LOGGER.error("Request validation failed.", exception);
        return buildModelAndView(exception);
    }

    /**
     * Default exception handler.
     *
     * @param exception the thrown exception
     * @return ModelAndView instance set to the error page view and including error message, and status
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView defaultExceptionHandler(Exception exception) {
        LOGGER.error("Unrecognized error occurred.", exception);
        return buildModelAndView(exception);
    }

    /**
     * Replaces ID placeholder in PATH_VIEW.
     *
     * @param id ID to replace placeholder to
     * @return path with ID inserted
     */
    String replaceIDInViewPath(Object id) {
        return PATH_VIEW.replace("{id}", String.valueOf(id));
    }

    /**
     * Function provider that properly populates redirection attributes on validation failure.
     *
     * @param redirectAttributes {@link RedirectAttributes} instance from the controller
     * @param formContent any object that contains form data causing the validation failure - can be used to repopulate form
     * @param redirectTo redirection path
     * @return Function instance that can be used to handle validation failure
     */
    Function<Map<String, String>, ModelAndView> validationFailureRedirectionSupplier(RedirectAttributes redirectAttributes, Object formContent, String redirectTo) {

        return (validationViolations) -> {
            redirectAttributes.addFlashAttribute(FLASH_MESSAGE, VALIDATION_FAILED_MESSAGE);
            redirectAttributes.addFlashAttribute(SUBMITTED_FORM_VALUES, formContent);
            redirectAttributes.addFlashAttribute(VALIDATION_RESULTS_ATTRIBUTE, validationViolations);
            redirectAttributes.addFlashAttribute(VALIDATION_FAILED_ATTRIBUTE, true);

            return modelAndViewFactory.createRedirectionTo(redirectTo);
        };
    }

    private Map<String, String> extractValidationViolations(ValidationErrorMessageListResponse validationErrorMessageListResponse) {
        return validationErrorMessageListResponse.getValidation().stream()
                .collect(Collectors.toMap(ValidationErrorMessageResponse::getField, ValidationErrorMessageResponse::getMessage));
    }

    private ModelAndView buildModelAndView(ValidationFailureException exception) {

        ModelAndView modelAndView = new ModelAndView(VALIDATION_ERROR_PAGE);
        modelAndView.addObject(VIEW_ATTRIBUTE_MESSAGE, exception.getMessage());
        modelAndView.addObject(VIEW_ATTRIBUTE_VALIDATION_ERRORS, exception.getErrorMessage().getValidation());
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);

        return modelAndView;
    }

    private ModelAndView buildModelAndView(DefaultNonSuccessfulResponseException exception) {

        ModelAndView modelAndView = new ModelAndView(DEFAULT_ERROR_PAGE);
        modelAndView.addObject(VIEW_ATTRIBUTE_MESSAGE, exception.getMessage());
        modelAndView.setStatus(HttpStatus.valueOf(exception.getStatus()));

        return modelAndView;
    }

    private ModelAndView buildModelAndView(Exception exception) {

        ModelAndView modelAndView = new ModelAndView(DEFAULT_ERROR_PAGE);
        modelAndView.addObject(VIEW_ATTRIBUTE_MESSAGE, exception.getMessage());
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        return modelAndView;
    }

    /**
     * Supplier interface for service calls that throws {@link CommunicationFailureException}.
     *
     * @param <T> response type of T
     */
    interface BridgeCallSupplier<T> {
        T get() throws CommunicationFailureException;
    }
}
