package hu.psprog.leaflet.lms.service.domain.common;

/**
 * Possible general response statues.
 * Actual value will be included in the ModelAndView.
 *
 * @author Peter Smith
 */
public enum ResponseStatus {

    /**
     * No processing errors, generated ModelAndView is represents successful request processing.
     */
    OK,

    /**
     * Request processing failed with validation error.
     */
    VALIDATION_FAILURE,

    /**
     * Error occurred while processing the request.
     */
    ERROR
}
