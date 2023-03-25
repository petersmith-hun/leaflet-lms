package hu.psprog.leaflet.lms.service.domain.entry;

import hu.psprog.leaflet.api.rest.request.entry.EntryInitialStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * Entry create/edit local request model.
 *
 * @author Peter Smith
 */
public class ModifyEntryRequest implements Serializable {

    @NotNull
    @NotEmpty
    @Size(max = 255)
    private String title;

    @NotNull
    @NotEmpty
    @Size(max = 255)
    private String link;

    @NotNull
    @NotEmpty
    private String prologue;

    @NotNull
    @NotEmpty
    private String rawContent;

    @NotNull
    @Min(1)
    private Long categoryID;

    @NotNull
    private Locale locale;

    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private boolean enabled;
    private EntryInitialStatus status = EntryInitialStatus.DRAFT;
    private List<Long> tags;
    private List<UUID> attachments;
    private Long userID;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setPrologue(String prologue) {
        this.prologue = prologue;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public void setCategoryID(Long categoryID) {
        this.categoryID = categoryID;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public void setMetaKeywords(String metaKeywords) {
        this.metaKeywords = metaKeywords;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setStatus(EntryInitialStatus status) {
        this.status = status;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }

    public void setAttachments(List<UUID> attachments) {
        this.attachments = attachments;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getPrologue() {
        return prologue;
    }

    public String getRawContent() {
        return rawContent;
    }

    public Long getCategoryID() {
        return categoryID;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public String getMetaKeywords() {
        return metaKeywords;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public EntryInitialStatus getStatus() {
        return status;
    }

    public List<Long> getTags() {
        return Optional.ofNullable(tags)
                .orElse(Collections.emptyList());
    }

    public List<UUID> getAttachments() {
        return Optional.ofNullable(attachments)
                .orElse(Collections.emptyList());
    }

    public Long getUserID() {
        return userID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ModifyEntryRequest that = (ModifyEntryRequest) o;

        return new EqualsBuilder()
                .append(enabled, that.enabled)
                .append(title, that.title)
                .append(link, that.link)
                .append(prologue, that.prologue)
                .append(rawContent, that.rawContent)
                .append(categoryID, that.categoryID)
                .append(locale, that.locale)
                .append(metaTitle, that.metaTitle)
                .append(metaDescription, that.metaDescription)
                .append(metaKeywords, that.metaKeywords)
                .append(status, that.status)
                .append(tags, that.tags)
                .append(attachments, that.attachments)
                .append(userID, that.userID)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(title)
                .append(link)
                .append(prologue)
                .append(rawContent)
                .append(categoryID)
                .append(locale)
                .append(metaTitle)
                .append(metaDescription)
                .append(metaKeywords)
                .append(enabled)
                .append(status)
                .append(tags)
                .append(attachments)
                .append(userID)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("title", title)
                .append("link", link)
                .append("prologue", prologue)
                .append("rawContent", rawContent)
                .append("categoryID", categoryID)
                .append("locale", locale)
                .append("metaTitle", metaTitle)
                .append("metaDescription", metaDescription)
                .append("metaKeywords", metaKeywords)
                .append("enabled", enabled)
                .append("status", status)
                .append("tags", tags)
                .append("attachments", attachments)
                .append("userID", userID)
                .toString();
    }
}
