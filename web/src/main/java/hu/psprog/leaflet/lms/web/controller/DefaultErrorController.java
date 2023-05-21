package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.bridge.client.exception.UnauthorizedAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

import static hu.psprog.leaflet.lms.web.controller.DefaultErrorController.PATH_ERROR;

/**
 * Default error controller.
 * Handles error occurred in filters as well.
 *
 * @author Peter Smith
 */
@Controller
@RequestMapping(PATH_ERROR)
public class DefaultErrorController implements ErrorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultErrorController.class);

    private static final String MESSAGE = "message";
    private static final String EXCEPTION = "exception";
    private static final String TRACE = "trace";
    private static final String STATUS = "status";
    private static final String ERROR = "error";

    static final String PATH_ERROR = "/error";

    private final ErrorAttributes errorAttributes;

    @Autowired
    public DefaultErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    /**
     * Renders default error page on uncaught exceptions happened before controllers.
     *
     * @param request {@link HttpServletRequest} object to extract exception info from
     * @return {@link ModelAndView} object set to the default error page view and populated with the error message
     */
    @RequestMapping
    public ModelAndView defaultErrorHandler(HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView(BaseController.DEFAULT_ERROR_PAGE);
        Map<String, Object> errorAttributes = extractErrorAttributes(request);

        modelAndView.addObject(MESSAGE, extractShortErrorMessage(errorAttributes));
        modelAndView.setStatus(extractStatus(errorAttributes));

        LOGGER.error("Unknown error occurred while processing request on path {}:\n{}", request.getServletPath(), extractTraceErrorMessage(errorAttributes));

        return modelAndView;
    }

    private Map<String, Object> extractErrorAttributes(HttpServletRequest request) {
        return errorAttributes.getErrorAttributes(new ServletWebRequest(request), ErrorAttributeOptions.of(ErrorAttributeOptions.Include.values()));
    }

    private HttpStatus extractStatus(Map<String, Object> errorAttributes) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (UnauthorizedAccessException.class.getName().equals(errorAttributes.get(EXCEPTION))) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (Objects.nonNull(errorAttributes.get(STATUS))) {
            status = HttpStatus.valueOf((Integer) errorAttributes.get(STATUS));
        }

        return status;
    }

    private String extractTraceErrorMessage(Map<String, Object> errorAttributes) {

        Object message = errorAttributes.get(TRACE);
        if (Objects.isNull(message)) {
            message = extractShortErrorMessage(errorAttributes);
        }

        return String.valueOf(message);
    }

    private String extractShortErrorMessage(Map<String, Object> errorAttributes) {
        return errorAttributes.get(ERROR) + ": " + errorAttributes.get(MESSAGE);
    }
}
