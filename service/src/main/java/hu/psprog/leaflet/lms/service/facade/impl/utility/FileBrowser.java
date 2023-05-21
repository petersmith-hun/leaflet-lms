package hu.psprog.leaflet.lms.service.facade.impl.utility;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lsrs.api.response.DirectoryDataModel;
import hu.psprog.leaflet.lsrs.api.response.DirectoryListDataModel;
import hu.psprog.leaflet.lsrs.api.response.FileDataModel;
import hu.psprog.leaflet.lsrs.api.response.FileListDataModel;
import hu.psprog.leaflet.lsrs.client.FileBridgeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Virtual file browser component.
 * Able to build up a virtual file system from the list of files and directories stored under
 * Leaflet's file storage. Only required parameter is a path string relative to the storage root
 * directory (which stays hidden).
 *
 * @author Peter Smith
 */
@Component
public class FileBrowser {

    private final FileBridgeService fileBridgeService;
    private final URLUtilities urlUtilities;

    @Autowired
    public FileBrowser(FileBridgeService fileBridgeService, URLUtilities urlUtilities) {
        this.fileBridgeService = fileBridgeService;
        this.urlUtilities = urlUtilities;
    }

    /**
     * Returns list of files stored under given path.
     *
     * @param path relative path
     * @return List of {@link FileDataModel} objects
     * @throws CommunicationFailureException if Bridge could not reach backend
     */
    public List<FileDataModel> getFiles(String path) throws CommunicationFailureException {

        String normalizedPath = urlUtilities.normalize(path);
        
        List<FileDataModel> files = Collections.emptyList();
        if (!StringUtils.EMPTY.equals(normalizedPath)) {
            FileListDataModel fileList = fileBridgeService.getUploadedFiles();
            files = fileList.files().stream()
                    .filter(fileDataModel -> {
                        String normalizedFilePath = urlUtilities.normalize(fileDataModel.path());
                        String[] filePathParts = urlUtilities.splitURL(normalizedFilePath);
                        String[] normalizedPathParts = urlUtilities.splitURL(normalizedPath);

                        return normalizedFilePath.startsWith(normalizedPath) && filePathParts.length == normalizedPathParts.length + 1;
                    })
                    .collect(Collectors.toList());
        }

        return files;
    }

    /**
     * Returns list of folders placed under given relative path.
     * Passing null or empty path value will return the list of acceptor root folders.
     * These folders are the absolute roots of the storage, no files can be can uploaded outside of them.
     *
     * @param path relative path
     * @return List of folder names as {@link String} objects
     * @throws CommunicationFailureException if Bridge could not reach backend
     */
    public List<String> getFolders(String path) throws CommunicationFailureException {

        List<String> folders;
        String normalizedPath = urlUtilities.normalize(path);
        DirectoryListDataModel directories = fileBridgeService.getDirectories();

        if (StringUtils.EMPTY.equals(normalizedPath)) {
            folders = directories.acceptors().stream()
                    .map(DirectoryDataModel::root)
                    .collect(Collectors.toList());
        } else {
            String[] normalizedPathParts = urlUtilities.splitURL(normalizedPath);
            folders = directories.acceptors().stream()
                    .filter(directoryDataModel -> directoryDataModel.root().equals(normalizedPathParts[0]))
                    .findFirst()
                    .map(DirectoryDataModel::children)
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(childDirectory -> {
                        String normalizedPathStart = urlUtilities.extractSubPath(1, 0, normalizedPath);
                        return !StringUtils.EMPTY.equals(childDirectory)
                                && urlUtilities.splitURL(childDirectory).length == normalizedPathParts.length
                                && (StringUtils.EMPTY.equals(normalizedPathStart) || childDirectory.startsWith(normalizedPathStart));
                    })
                    .map(childDirectory -> {
                        String[] subPathParts = urlUtilities.splitURL(childDirectory);
                        return subPathParts[subPathParts.length - 1];
                    })
                    .collect(Collectors.toList());

        }

        return folders;
    }
}
