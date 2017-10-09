package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

import java.util.List;

/**
 * File operations facade.
 *
 * @author Peter Smith
 */
public interface FileFacade {

    /**
     * Returns list of uploaded files.
     *
     * @return list of uploaded files as {@link FileDataModel}
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    List<FileDataModel> getUploadedFiles() throws CommunicationFailureException;
}
