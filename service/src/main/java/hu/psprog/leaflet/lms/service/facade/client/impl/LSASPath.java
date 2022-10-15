package hu.psprog.leaflet.lms.service.facade.client.impl;

import hu.psprog.leaflet.bridge.client.request.Path;

/**
 * Available paths of LSAS.
 *
 * @author Peter Smith
 */
public enum LSASPath implements Path {

    REGISTERED_SERVICES("/stack-status/registered-services"),
    REGISTRY("/registry"),
    REGISTRY_REPOSITORIES("/registry/{registryID}"),
    REGISTRY_REPOSITORIES_TAGS("/registry/{registryID}/{repositoryID}"),
    REGISTRY_GROUPED_REPOSITORIES_TAGS("/registry/{registryID}/{groupID}/{repositoryID}"),
    REGISTRY_REPOSITORIES_TAGS_TAG("/registry/{registryID}/{repositoryID}/{tagID}"),
    REGISTRY_GROUPED_REPOSITORIES_TAGS_TAG("/registry/{registryID}/{groupID}/{repositoryID}/{tagID}"),
    CONTAINERS("/containers");

    private final String uri;

    LSASPath(String uri) {
        this.uri = uri;
    }

    @Override
    public String getURI() {
        return uri;
    }
}
