package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.file.DirectoryCreationRequestModel;
import hu.psprog.leaflet.api.rest.request.file.FileUploadRequestModel;
import hu.psprog.leaflet.api.rest.request.file.UpdateFileMetaInfoRequestModel;
import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.FileBridgeService;
import hu.psprog.leaflet.lms.service.domain.file.FilesByFolder;
import hu.psprog.leaflet.lms.service.facade.FileFacade;
import hu.psprog.leaflet.lms.service.facade.impl.utility.FileBrowser;
import hu.psprog.leaflet.lms.service.facade.impl.utility.URLUtilities;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of {@link FileFacade}.
 *
 * @author Peter Smith
 */
@Service
public class FileFacadeImpl implements FileFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileFacadeImpl.class);

    private FileBridgeService fileBridgeService;
    private FileBrowser fileBrowser;
    private URLUtilities urlUtilities;

    @Autowired
    public FileFacadeImpl(FileBridgeService fileBridgeService, FileBrowser fileBrowser, URLUtilities urlUtilities) {
        this.fileBridgeService = fileBridgeService;
        this.fileBrowser = fileBrowser;
        this.urlUtilities = urlUtilities;
    }

    @Override
    public List<FileDataModel> getUploadedFiles() throws CommunicationFailureException {
        return Optional.ofNullable(fileBridgeService.getUploadedFiles().getFiles())
                .orElse(Collections.emptyList());
    }

    @Override
    public FilesByFolder getFilesByFolder(String path) throws CommunicationFailureException {

        return FilesByFolder.getBuilder()
                .withSubFolders(fileBrowser.getFolders(path))
                .withFiles(fileBrowser.getFiles(path))
                .build();
    }

    @Override
    public byte[] getFileForDownload(UUID pathUUID, String filename) throws CommunicationFailureException {

        byte[] content = new byte[0];
        InputStream fileStream = fileBridgeService.downloadFile(pathUUID, filename);
        try {
            content = IOUtils.toByteArray(fileStream);
        } catch (Exception e) {
            LOGGER.error("Failed to convert received file [{}] to byte array.", pathUUID);
        }

        return content;
    }

    @Override
    public FileDataModel getFileDetails(UUID pathUUID) throws CommunicationFailureException {
        return fileBridgeService.getFileDetails(pathUUID);
    }

    @Override
    public List<String> getAcceptableMimeTypes(String path) throws CommunicationFailureException {

        String normalizedPath = urlUtilities.normalize(path);

        return fileBridgeService.getDirectories().getAcceptors().stream()
                .filter(directoryDataModel -> normalizedPath.startsWith(directoryDataModel.getRoot()))
                .flatMap(directoryDataModel -> directoryDataModel.getAcceptableMimeTypes().stream())
                .collect(Collectors.toList());
    }

    @Override
    public UUID processFileUpload(FileUploadRequestModel fileUploadRequestModel) throws CommunicationFailureException {
        return Optional.ofNullable(fileBridgeService.uploadFile(fileUploadRequestModel))
                .map(urlUtilities::extractFilePathUUID)
                .orElse(null);
    }

    @Override
    public void processUpdateFileMetaInfo(UUID pathUUID, UpdateFileMetaInfoRequestModel updateFileMetaInfoRequestModel) throws CommunicationFailureException {
        fileBridgeService.updateFileMetaInfo(pathUUID, updateFileMetaInfoRequestModel);
    }

    @Override
    public void processCreateDirectory(DirectoryCreationRequestModel directoryCreationRequestModel) throws CommunicationFailureException {
        fileBridgeService.createDirectory(directoryCreationRequestModel);
    }

    @Override
    public void processDeleteFile(UUID pathUUID) throws CommunicationFailureException {
        fileBridgeService.deleteFile(pathUUID);
    }
}
