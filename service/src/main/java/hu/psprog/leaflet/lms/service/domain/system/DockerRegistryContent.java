package hu.psprog.leaflet.lms.service.domain.system;

import java.util.Collections;
import java.util.List;

/**
 * Domain class representing a Docker containing the list of repositories in a registry.
 *
 * @author Peter Smith
 */
public record DockerRegistryContent(
        String registryName,
        List<String> repositories
) {

    public static final DockerRegistryContent FALLBACK_DOCKER_REGISTRY_CONTENT =
            new DockerRegistryContent("unknown", Collections.emptyList());
}
