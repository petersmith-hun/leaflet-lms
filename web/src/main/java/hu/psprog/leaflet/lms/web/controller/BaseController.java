package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.bridge.client.exception.DefaultNonSuccessfulResponseException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.lms.service.auth.JWTTokenAuthentication;
import hu.psprog.leaflet.lms.service.auth.user.AuthenticationUserDetailsModel;
import hu.psprog.leaflet.lms.web.factory.ModelAndViewFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

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

    static final String DEFAULT_ERROR_PAGE = "view/error/default";

    static final String PATH_HOME = "/";
    static final String PATH_CREATE = "/create";
    static final String PATH_VIEW = "/view/{id}";
    static final String PATH_EDIT = "/edit/{id}";
    static final String PATH_STATUS = "/status/{id}";
    static final String PATH_DELETE = "/delete/{id}";
    static final String PATH_OPTIONAL_PAGE_NUMBER = "/{page}";
    static final String PATH_CURRENT = "";

    static final String PATH_VARIABLE_ID = "id";
    static final String PATH_VARIABLE_TOKEN = "token";

    static final String FLASH_MESSAGE = "flash";

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
        if (authentication instanceof JWTTokenAuthentication) {
            userID = ((AuthenticationUserDetailsModel) authentication.getDetails()).getId();
        }

        return userID;
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
}
