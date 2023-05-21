package hu.psprog.leaflet.lms.service.extraction;

import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Data extractors.
 *
 * @author Peter Smith
 */
@Component
public class CommonExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonExtractor.class);

    private static final String UUID_EXTRACTION_FAILURE = "Failed to extract UUID from given file reference [%s]. Reference value must match '/<UUID>/filename.ext'";

    /**
     * Extracts UUID from {@link FileDataModel}.
     *
     * @param fileDataModel {@link FileDataModel} object holding file information
     * @return UUID extracted from {@link FileDataModel}
     */
    public UUID extractPathUUIDFromReference(FileDataModel fileDataModel) {
        return extractPathUUIDFromReference(fileDataModel.reference());
    }

    /**
     * Extracts UUID from file path reference.
     *
     * @param fileReference original file reference
     * @return UUID extracted from reference
     */
    public UUID extractPathUUIDFromReference(String fileReference) {
        try {
            return UUID.fromString(fileReference.split("/")[1]);
        } catch (Exception exc) {
            String message = String.format(UUID_EXTRACTION_FAILURE, fileReference);
            LOGGER.error(message, exc);
            throw new IllegalArgumentException(message);
        }
    }
}
