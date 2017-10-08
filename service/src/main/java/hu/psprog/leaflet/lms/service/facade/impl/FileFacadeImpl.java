package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.FileBridgeService;
import hu.psprog.leaflet.lms.service.facade.FileFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link FileFacade}.
 *
 * @author Peter Smith
 */
@Service
public class FileFacadeImpl implements FileFacade {

    private FileBridgeService fileBridgeService;

    @Autowired
    public FileFacadeImpl(FileBridgeService fileBridgeService) {
        this.fileBridgeService = fileBridgeService;
    }

    @Override
    public List<FileDataModel> getUploadedFiles() throws CommunicationFailureException {
        return Optional.ofNullable(fileBridgeService.getUploadedFiles().getFiles())
                .orElse(Collections.emptyList());
    }
}
