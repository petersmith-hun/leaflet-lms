package hu.psprog.leaflet.lms.web.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

/**
 * Debugger interceptor to log parameters added to the current model.
 *
 * @author Peter Smith
 */
@Component
@Profile({"development", "debug"})
public class ModelAndViewDebuggerInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelAndViewDebuggerInterceptor.class);
    private static final String MODEL_ENTRY_PATTERN = "%60s : %s";
    private static final String DEBUG_MESSAGE_PREFIX = "Model contents" + System.lineSeparator();
    private static final String DEBUG_MESSAGE_SUFFIX = System.lineSeparator();

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
        LOGGER.info(createModelContentDebugString(modelAndView));
    }

    private String createModelContentDebugString(ModelAndView modelAndView) {
        return modelAndView.getModel().entrySet().stream()
                .map(entry -> String.format(MODEL_ENTRY_PATTERN, entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(System.lineSeparator(), DEBUG_MESSAGE_PREFIX, DEBUG_MESSAGE_SUFFIX));
    }
}
