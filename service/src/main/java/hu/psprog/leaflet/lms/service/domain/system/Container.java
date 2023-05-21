package hu.psprog.leaflet.lms.service.domain.system;

import lombok.Builder;

import java.util.List;

/**
 * Domain class representing a Docker container's base information set.
 *
 * @author Peter Smith
 */
@Builder
public record Container(
        String id,
        String image,
        List<String> names,
        String state
) { }
