package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.FileFacade;
import hu.psprog.leaflet.lms.service.facade.impl.utility.URLUtilities;
import hu.psprog.leaflet.lsrs.api.request.DirectoryCreationRequestModel;
import hu.psprog.leaflet.lsrs.api.request.FileUploadRequestModel;
import hu.psprog.leaflet.lsrs.api.request.UpdateFileMetaInfoRequestModel;
import hu.psprog.leaflet.lsrs.api.response.FileDataModel;
import hu.psprog.leaflet.lsrs.api.response.VFSBrowserModel;
import hu.psprog.leaflet.lsrs.client.FileBridgeService;
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

    private final FileBridgeService fileBridgeService;
    private final URLUtilities urlUtilities;

    @Autowired
    public FileFacadeImpl(FileBridgeService fileBridgeService, URLUtilities urlUtilities) {
        this.fileBridgeService = fileBridgeService;
        this.urlUtilities = urlUtilities;
    }

    @Override
    public List<FileDataModel> getUploadedFiles() throws CommunicationFailureException {
        return Optional.ofNullable(fileBridgeService.getUploadedFiles().files())
                .orElse(Collections.emptyList());
    }

    @Override
    public VFSBrowserModel browse(String path) throws CommunicationFailureException {

        String normalizedPath = path.startsWith("/")
                ? path.substring(1)
                : path;

        return fileBridgeService.browse(normalizedPath);
    }

    @Override
    public byte[] getFileForDownload(UUID pathUUID, String filename) throws CommunicationFailureException {

        byte[] content = new byte[0];
        InputStream fileStream = fileBridgeService.downloadFile(pathUUID, filename);
        try {
            content = IOUtils.toByteArray(fileStream);
            fileStream.close();
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

        return fileBridgeService.getDirectories().acceptors().stream()
                .filter(directoryDataModel -> normalizedPath.startsWith(directoryDataModel.root()))
                .flatMap(directoryDataModel -> directoryDataModel.acceptableMimeTypes().stream())
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
