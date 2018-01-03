package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.api.rest.request.file.DirectoryCreationRequestModel;
import hu.psprog.leaflet.api.rest.request.file.FileUploadRequestModel;
import hu.psprog.leaflet.api.rest.request.file.UpdateFileMetaInfoRequestModel;
import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.file.FilesByFolder;

import java.util.List;
import java.util.UUID;

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

    /**
     * Returns list of files stored under given folder.
     *
     * @param path path to the folder
     * @return list of files wrapped in {@link FilesByFolder}
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    FilesByFolder getFilesByFolder(String path) throws CommunicationFailureException;

    /**
     * Retrieves a file resource for download.
     *
     * @param pathUUID file identifier UUID
     * @param filename filename
     * @return resource as byte array
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    byte[] getFileForDownload(UUID pathUUID, String filename) throws CommunicationFailureException;

    /**
     * Returns file meta information for given identifier.
     *
     * @param pathUUID file identifier UUID
     * @return file meta information as {@link FileDataModel}
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    FileDataModel getFileDetails(UUID pathUUID) throws CommunicationFailureException;

    /**
     * Returns acceptable MIME types for given path.
     *
     * @param path current virtual file system path
     * @return list of acceptable MIME types for current folder
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    List<String> getAcceptableMimeTypes(String path) throws CommunicationFailureException;

    /**
     * Processes file upload request.
     *
     * @param fileUploadRequestModel file data
     * @return UUID of created file
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    UUID processFileUpload(FileUploadRequestModel fileUploadRequestModel) throws CommunicationFailureException;

    /**
     * Processes file meta information update request.
     *
     * @param pathUUID file identifier UUID
     * @param updateFileMetaInfoRequestModel updated file meta information
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void processUpdateFileMetaInfo(UUID pathUUID, UpdateFileMetaInfoRequestModel updateFileMetaInfoRequestModel) throws CommunicationFailureException;

    /**
     * Processes directory creation request.
     *
     * @param directoryCreationRequestModel directory data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void processCreateDirectory(DirectoryCreationRequestModel directoryCreationRequestModel) throws CommunicationFailureException;

    /**
     * Process file deletion request.
     *
     * @param pathUUID file identifier UUID
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void processDeleteFile(UUID pathUUID) throws CommunicationFailureException;
}
