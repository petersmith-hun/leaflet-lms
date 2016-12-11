package hu.psprog.leaflet.lms.web.response.model.entry;

/**
 * @author Peter Smith
 */
public class EntryResponseModel {

    private String title;
    private String prologue;
    private String rawContent;
    private String link;
    private String created;
    private String lastModified;
    private Integer id;
    private String author;
    private Integer categoryID;

    private EntryResponseModel() {
        // prevent direct initialization
    }

    public String getTitle() {
        return title;
    }

    public String getPrologue() {
        return prologue;
    }

    public String getRawContent() {
        return rawContent;
    }

    public String getLink() {
        return link;
    }

    public String getCreated() {
        return created;
    }

    public String getLastModified() {
        return lastModified;
    }

    public Integer getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public Integer getCategoryID() {
        return categoryID;
    }

    public static class Builder {

        private EntryResponseModel entryResponseModel;

        public Builder() {
            entryResponseModel = new EntryResponseModel();
        }

        public Builder withTitle(String title) {
            entryResponseModel.title = title;
            return this;
        }

        public Builder withPrologue(String prologue) {
            entryResponseModel.prologue = prologue;
            return this;
        }

        public Builder withRawContent(String rawContent) {
            entryResponseModel.rawContent = rawContent;
            return this;
        }

        public Builder withLink(String link) {
            entryResponseModel.link = link;
            return this;
        }

        public Builder withCreated(String created) {
            entryResponseModel.created = created;
            return this;
        }

        public Builder withLastModified(String lastModified) {
            entryResponseModel.lastModified = lastModified;
            return this;
        }

        public Builder withID(Integer id) {
            entryResponseModel.id = id;
            return this;
        }

        public Builder withAuthor(String author) {
            entryResponseModel.author = author;
            return this;
        }

        public Builder withCategoryID(Integer categoryID) {
            entryResponseModel.categoryID = categoryID;
            return this;
        }

        public EntryResponseModel build() {
            return entryResponseModel;
        }
    }
}
