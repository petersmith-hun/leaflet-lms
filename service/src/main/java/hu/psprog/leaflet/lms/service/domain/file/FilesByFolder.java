package hu.psprog.leaflet.lms.service.domain.file;

import hu.psprog.leaflet.lsrs.api.response.FileDataModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * Domain object that holds list of files and sub folders under a folder.
 *
 * @author Peter Smith
 */
public class FilesByFolder {

    private List<String> subFolders;
    private List<FileDataModel> files;

    public List<String> getSubFolders() {
        return subFolders;
    }

    public List<FileDataModel> getFiles() {
        return files;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FilesByFolder that = (FilesByFolder) o;

        return new EqualsBuilder()
                .append(subFolders, that.subFolders)
                .append(files, that.files)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(subFolders)
                .append(files)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("subFolders", subFolders)
                .append("files", files)
                .toString();
    }

    public static FilesByFolderBuilder getBuilder() {
        return new FilesByFolderBuilder();
    }

    /**
     * Builder for {@link FilesByFolder).
     */
    public static final class FilesByFolderBuilder {
        private List<String> subFolders;
        private List<FileDataModel> files;

        private FilesByFolderBuilder() {
        }

        public FilesByFolderBuilder withSubFolders(List<String> subFolders) {
            this.subFolders = subFolders;
            return this;
        }

        public FilesByFolderBuilder withFiles(List<FileDataModel> files) {
            this.files = files;
            return this;
        }

        public FilesByFolder build() {
            FilesByFolder filesByFolder = new FilesByFolder();
            filesByFolder.files = this.files;
            filesByFolder.subFolders = this.subFolders;
            return filesByFolder;
        }
    }
}
