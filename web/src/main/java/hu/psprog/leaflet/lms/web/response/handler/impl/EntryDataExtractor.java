package hu.psprog.leaflet.lms.web.response.handler.impl;

import hu.psprog.leaflet.lms.web.response.model.entry.EntryResponseModel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * Extracts a single {@link EntryResponseModel}.
 *
 * @author Peter Smith
 */
@Component
public class EntryDataExtractor extends AbstractResponseDataExtractor<EntryResponseModel> {

    private static final String TITLE = "title";
    private static final String PROLOGUE = "prologue";
    private static final String RAW_CONTENT = "rawContent";
    private static final String LINK = "link";
    private static final String CREATED = "created";
    private static final String LAST_MODIFIED = "lastModified";
    private static final String ID = "id";
    private static final String USER = "user";
    private static final String USERNAME = "username";
    private static final String CATEGORY = "category";

    @Override
    public EntryResponseModel extract(Map<String, Object> response) {

        EntryResponseModel.Builder builder = new EntryResponseModel.Builder();
        Map<String, Object> content = extractBody(response);
        if (Objects.nonNull(content)) {
            builder.withTitle(extractString(content, TITLE))
                    .withPrologue(extractString(content, PROLOGUE))
                    .withRawContent(extractString(content, RAW_CONTENT))
                    .withLink(extractString(content, LINK))
                    .withCreated(extractString(content, CREATED))
                    .withLastModified(extractString(content, LAST_MODIFIED))
                    .withID(extractInteger(content, ID))
                    .withAuthor(extractString(extractNode(content, USER), USERNAME))
                    .withCategoryID(extractInteger(extractNode(content, CATEGORY), ID));
        }

        return builder.build();
    }
}
