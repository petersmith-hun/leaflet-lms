package hu.psprog.leaflet.lms.service.domain.dashboard;

import lombok.Builder;

import java.util.Map;

/**
 * {@link JSClientHelperModel} implementation holding information for stack status related HTTP calls.
 *
 * @author Peter Smith
 */
@Builder
public record StackStatusJSClientHelperModel(
        boolean enabled,
        Map<String, String> authorization,
        String discoverEndpoint
) implements JSClientHelperModel { }
