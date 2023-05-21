package hu.psprog.leaflet.lms.service.domain.file;

import hu.psprog.leaflet.lsrs.api.response.FileDataModel;
import lombok.Builder;

import java.util.List;

/**
 * Domain object that holds list of files and sub folders under a folder.
 *
 * @author Peter Smith
 */
@Builder
public record FilesByFolder(
        List<String> subFolders,
        List<FileDataModel> files
) { }
