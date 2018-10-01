package hu.psprog.leaflet.lms.web.interceptor;

import hu.psprog.leaflet.lms.service.domain.common.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

/**
 * Interceptor implementation to set general status flag in {@link ModelAndView}.
 * Behavior:
 *  - If the currently generated view is a redirection, status won't be set.
 *  - If validation were are registered on flash scope, or response status is BAD_REQUEST, general status will be VALIDATION_FAILURE.
 *  - Upon any other 4xx and 5xx response statuses will cause the general status to be set to ERROR.
 *  - Otherwise general status is OK.
 *
 * @author Peter Smith
 */
@Component
public class GeneralStatusSetterInterceptor extends HandlerInterceptorAdapter {

    public static final String VALIDATION_FAILED_ATTRIBUTE = "validationFailed";

    private static final String STATUS_ATTRIBUTE = "generalStatus";
    private static final String REDIRECTION_VIEW_PREFIX = "redirect:";

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);

        if (Objects.nonNull(modelAndView) && !isRedirection(modelAndView)) {
            setGeneralStatus(modelAndView);
        }
    }

    private boolean isRedirection(ModelAndView modelAndView) {
        return modelAndView.getViewName().startsWith(REDIRECTION_VIEW_PREFIX);
    }

    private void setGeneralStatus(ModelAndView modelAndView) {

        ResponseStatus responseStatus;
        if (validationFailureRegistered(modelAndView)) {
            responseStatus = ResponseStatus.VALIDATION_FAILURE;
        } else {
            responseStatus = getResponseStatus(modelAndView);
        }

        modelAndView.addObject(STATUS_ATTRIBUTE, responseStatus);
    }

    private ResponseStatus getResponseStatus(ModelAndView modelAndView) {

        ResponseStatus responseStatus = ResponseStatus.OK;
        if (isBadRequest(modelAndView)) {
            responseStatus = ResponseStatus.VALIDATION_FAILURE;
        } else if (isAnyErrorResponse(modelAndView)) {
            responseStatus = ResponseStatus.ERROR;
        }

        return responseStatus;
    }

    private boolean isBadRequest(ModelAndView modelAndView) {
        return modelAndView.getStatus() == HttpStatus.BAD_REQUEST;
    }

    private boolean isAnyErrorResponse(ModelAndView modelAndView) {
        return Optional.ofNullable(modelAndView.getStatus())
                .map(status -> status.is4xxClientError() || status.is5xxServerError())
                .orElse(false);
    }

    private boolean validationFailureRegistered(ModelAndView modelAndView) {
        return modelAndView.getModel().containsKey(VALIDATION_FAILED_ATTRIBUTE);
    }
}
