package hu.psprog.leaflet.lms.service.domain.system;

import java.util.Collections;
import java.util.List;

/**
 * Domain class representing a Docker repository base information object.
 *
 * @author Peter Smith
 */
public record DockerRepository(
        String registry,
        String name,
        List<DockerTag> tags
) {

    public static final DockerRepository FALLBACK_DOCKER_REPOSITORY =
            new DockerRepository("unknown", "unknown", Collections.emptyList());
}
