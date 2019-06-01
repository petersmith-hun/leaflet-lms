package hu.psprog.leaflet.lms.web.support.thymeleaf;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Customr {@link Formatter} implementation used by Thymeleaf to format {@link ZonedDateTime} while rendering templates.
 *
 * @author Peter Smith
 */
public class ZonedDateTimeFormatter implements Formatter<ZonedDateTime> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy. LLLL dd., EEEE, HH:mm");

    @Override
    public ZonedDateTime parse(String text, Locale locale) throws ParseException {
        // not needed for now
        return null;
    }

    @Override
    public String print(ZonedDateTime object, Locale locale) {
        return DATE_TIME_FORMATTER
                .withZone(ZoneId.systemDefault())
                .withLocale(locale)
                .format(object);
    }
}
