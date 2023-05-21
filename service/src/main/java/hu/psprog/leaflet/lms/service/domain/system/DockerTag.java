package hu.psprog.leaflet.lms.service.domain.system;

import java.time.ZonedDateTime;

/**
 * Domain class representing a Docker image tag.
 *
 * @author Peter Smith
 */
public record DockerTag(
        String name,
        ZonedDateTime created
) { }
