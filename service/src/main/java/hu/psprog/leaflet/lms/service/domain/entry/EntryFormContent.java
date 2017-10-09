package hu.psprog.leaflet.lms.service.domain.entry;

import hu.psprog.leaflet.api.rest.response.category.CategoryDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import hu.psprog.leaflet.api.rest.response.tag.TagDataModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * Wrapper model for Entry creation/edit form data.
 *
 * @author Peter Smith
 */
public class EntryFormContent {

    private List<TagDataModel> existingTags;
    private List<CategoryDataModel> existingCategories;
    private List<FileDataModel> existingFiles;
    private WrapperBodyDataModel<EditEntryDataModel> entryData;

    public List<TagDataModel> getExistingTags() {
        return existingTags;
    }

    public List<CategoryDataModel> getExistingCategories() {
        return existingCategories;
    }

    public List<FileDataModel> getExistingFiles() {
        return existingFiles;
    }

    public WrapperBodyDataModel<EditEntryDataModel> getEntryData() {
        return entryData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EntryFormContent that = (EntryFormContent) o;

        return new EqualsBuilder()
                .append(existingTags, that.existingTags)
                .append(existingCategories, that.existingCategories)
                .append(existingFiles, that.existingFiles)
                .append(entryData, that.entryData)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(existingTags)
                .append(existingCategories)
                .append(existingFiles)
                .append(entryData)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("existingTags", existingTags)
                .append("existingCategories", existingCategories)
                .append("existingFiles", existingFiles)
                .append("entryData", entryData)
                .toString();
    }

    public static EntryFormContentBuilder getBuilder() {
        return new EntryFormContentBuilder();
    }

    /**
     * Builder for {@link EntryFormContent}.
     */
    public static final class EntryFormContentBuilder {
        private List<TagDataModel> existingTags;
        private List<CategoryDataModel> existingCategories;
        private List<FileDataModel> existingFiles;
        private WrapperBodyDataModel<EditEntryDataModel> entryData;

        private EntryFormContentBuilder() {
        }

        public EntryFormContentBuilder withExistingTags(List<TagDataModel> existingTags) {
            this.existingTags = existingTags;
            return this;
        }

        public EntryFormContentBuilder withExistingCategories(List<CategoryDataModel> existingCategories) {
            this.existingCategories = existingCategories;
            return this;
        }

        public EntryFormContentBuilder withExistingFiles(List<FileDataModel> existingFiles) {
            this.existingFiles = existingFiles;
            return this;
        }

        public EntryFormContentBuilder withEntryData(WrapperBodyDataModel<EditEntryDataModel> entryData) {
            this.entryData = entryData;
            return this;
        }

        public EntryFormContent build() {
            EntryFormContent entryFormContent = new EntryFormContent();
            entryFormContent.existingCategories = this.existingCategories;
            entryFormContent.existingFiles = this.existingFiles;
            entryFormContent.entryData = this.entryData;
            entryFormContent.existingTags = this.existingTags;
            return entryFormContent;
        }
    }
}
