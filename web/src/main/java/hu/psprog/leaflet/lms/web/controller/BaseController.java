package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.bridge.client.exception.DefaultNonSuccessfulResponseException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import org.springframework.http.HttpStatus;
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

    private static final String VIEW_ATTRIBUTE_MESSAGE = "message";
    private static final String VALIDATION_ERROR_PAGE = "view/error/validation";
    static final String DEFAULT_ERROR_PAGE = "view/error/default";

    /**
     * Handles any type of exceptions thrown by Bridge (except for {@link ValidationFailureException}).
     *
     * @param exception the thrown exception
     * @return ModelAndView instance set to the error page view and including error message, and status
     */
    @ExceptionHandler(DefaultNonSuccessfulResponseException.class)
    public ModelAndView bridgeNonSuccessfulResponseExceptionHandler(DefaultNonSuccessfulResponseException exception) {
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
        return buildModelAndView(exception);
    }

    private ModelAndView buildModelAndView(ValidationFailureException exception) {

        ModelAndView modelAndView = new ModelAndView(VALIDATION_ERROR_PAGE);
        modelAndView.addObject(VIEW_ATTRIBUTE_MESSAGE, exception.getErrorMessage().getValidation());
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

        return modelAndView;
    }
}
