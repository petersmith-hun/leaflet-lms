package hu.psprog.leaflet.lms.service.facade.impl.utility;

import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * File path UUID extractor tool.
 *
 * @author Peter Smith
 */
@Component
public class URLUtilities {

    private static final String CHAR_SLASH = "/";
    private static final String CHAR_BACKSLASH = "\\";

    private AntPathMatcher antPathMatcher;

    public URLUtilities() {
        this.antPathMatcher = new AntPathMatcher();
    }

    /**
     * Extracts URL part with given pattern.
     *
     * @param pattern pattern for extraction
     * @param url URL to use for extraction
     * @return extracted URL part
     */
    public String extractSubPath(String pattern, String url) {
        return antPathMatcher.extractPathWithinPattern(pattern, url);
    }

    /**
     * Extracts URL part from between given inclusive levels.
     *
     * @param levelFromBeginning level from the beginning
     * @param levelFromEnding level from the ending
     * @param url URL to use for extraction
     * @return extracted URL part
     */
    public String extractSubPath(int levelFromBeginning, int levelFromEnding, String url) {
        return buildURLFromParts(splitURL(url), levelFromBeginning, levelFromEnding);
    }

    /**
     * Extracts path UUID from file reference string.
     *
     * @param fileDataModel {@link FileDataModel} that holds the reference string
     * @return extracted UUID
     */
    public UUID extractFilePathUUID(FileDataModel fileDataModel) {

        String normalizedReference = fileDataModel.getReference().startsWith(CHAR_SLASH)
                ? fileDataModel.getReference()
                : CHAR_SLASH + fileDataModel.getReference();

        return UUID.fromString(normalizedReference.split(CHAR_SLASH)[1]);
    }

    /**
     * Normalizes URL by adding starting and ending slash characters (if not present already).
     *
     * @param rawURL URL string to normalize
     * @return normalized URL string
     */
    public String normalize(String rawURL) {
        return Optional.ofNullable(rawURL)
                .map(url -> removeStartingSlash(removeEndingSlash(normalizeSlashes(url))))
                .orElse(StringUtils.EMPTY);
    }

    /**
     * Returns parent URL of given URL string.
     *
     * @param url URL to extract parent for
     * @param disableAtLevel level number as integer to disable parent URL generating
     * @return parent URL of given URL
     */
    public String getUpURL(String url, int disableAtLevel) {

        String[] urlParts = splitURL(url);
        return urlParts.length == disableAtLevel
                ? normalize(url)
                : buildURLFromParts(urlParts, 0, 1);
    }

    /**
     * Splits given URL at slashes.
     *
     * @param url URL to split
     * @return URL parts as array
     */
    public String[] splitURL(String url) {
        return normalize(url).split(CHAR_SLASH);
    }

    private String buildURLFromParts(String[] urlParts, int levelFromBeginning, int levelFromEnding) {

        String builtURL = StringUtils.EMPTY;
        if (levelFromBeginning < urlParts.length && levelFromEnding < urlParts.length) {
            builtURL = Arrays
                    .stream(Arrays.copyOfRange(urlParts, levelFromBeginning, urlParts.length - levelFromEnding))
                    .collect(Collectors.joining(CHAR_SLASH));
        }

        return builtURL;
    }

    private String removeEndingSlash(String url) {
        return url.endsWith(CHAR_SLASH)
                ? url.substring(0, url.length() - 1)
                : url;
    }

    private String removeStartingSlash(String url) {
        return url.startsWith(CHAR_SLASH)
                ? url.substring(1, url.length())
                : url;
    }

    private String normalizeSlashes(String url) {
        return url.replace(CHAR_BACKSLASH, CHAR_SLASH);
    }
}
