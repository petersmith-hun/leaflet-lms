package hu.psprog.leaflet.lms.web.interceptor;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Interceptor implementation that adds common variable to the model.
 *
 * @author Peter Smith
 */
@Component
public class CommonPageDataInterceptor implements HandlerInterceptor {

    private static final String APP_VERSION_ATTRIBUTE = "footerAppVersion";
    private static final String COMMON_ATTRIBUTE = "common";
    private static final String DEFAULT_VERSION = "snapshot";

    private final Map<String, String> commonPageData;

    @Autowired
    public CommonPageDataInterceptor(Optional<BuildProperties> optionalBuildProperties) {
        commonPageData = Map.of(APP_VERSION_ATTRIBUTE, optionalBuildProperties.map(BuildProperties::getVersion).orElse(DEFAULT_VERSION));
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

        if (isRequestDispatcher(request) && Objects.nonNull(modelAndView)) {
            setApplicationVersion(modelAndView);
            modelAndView.addObject("request", request);
            modelAndView.addObject("response", response);
        }
    }

    private boolean isRequestDispatcher(HttpServletRequest request) {
        return request.getDispatcherType() == DispatcherType.REQUEST;
    }

    private void setApplicationVersion(ModelAndView modelAndView) {
        modelAndView.addObject(COMMON_ATTRIBUTE, commonPageData);
    }
}
