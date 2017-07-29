package hu.psprog.leaflet.lms.web.auth.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * Filter to include device ID for all requests.
 * Generated device ID belongs to the user session, and it stays the same for the entire time the session is alive.
 *
 * @author Peter Smith
 */
@Component
@Order(HIGHEST_PRECEDENCE + 100)
public class DeviceIDFilter extends GenericFilterBean {

    private static final String DEVICE_ID_HEADER = "X-Device-ID";

    private DeviceIDGenerator deviceIDGenerator;

    @Autowired
    public DeviceIDFilter(DeviceIDGenerator deviceIDGenerator) {
        this.deviceIDGenerator = deviceIDGenerator;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setAttribute(DEVICE_ID_HEADER, deviceIDGenerator.getID());
        chain.doFilter(request, response);
    }
}
