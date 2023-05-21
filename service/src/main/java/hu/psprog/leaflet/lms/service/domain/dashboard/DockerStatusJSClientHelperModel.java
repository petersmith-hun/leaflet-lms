package hu.psprog.leaflet.lms.service.domain.dashboard;

import lombok.Builder;

import java.util.Map;

/**
 * {@link JSClientHelperModel} implementation holding information for Docker status related HTTP calls.
 *
 * @author Peter Smith
 */
@Builder
public record DockerStatusJSClientHelperModel(
        boolean enabled,
        Map<String, String> authorization,
        String statusEndpoint,
        String detailsEndpoint
) implements JSClientHelperModel { }
