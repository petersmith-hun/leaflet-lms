package hu.psprog.leaflet.lms.service.domain.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * {@link JSClientHelperModel} implementation holding information for stack status related HTTP calls.
 *
 * @author Peter Smith
 */
@Data
@Builder
public class StackStatusJSClientHelperModel implements JSClientHelperModel {

    private final boolean enabled;
    private final Map<String, String> authorization;
    private final String discoverEndpoint;
}
