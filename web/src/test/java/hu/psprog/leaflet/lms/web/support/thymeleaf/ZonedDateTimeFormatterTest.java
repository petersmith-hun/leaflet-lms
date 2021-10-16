package hu.psprog.leaflet.lms.web.support.thymeleaf;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link ZonedDateTimeFormatter}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class ZonedDateTimeFormatterTest {

    private static TimeZone defaultTimeZone;

    @InjectMocks
    private ZonedDateTimeFormatter zonedDateTimeFormatter;

    @BeforeAll
    public static void setup() {
        defaultTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+2"));
    }

    @AfterAll
    public static void tearDown() {
        TimeZone.setDefault(defaultTimeZone);
    }

    @Test
    public void shouldParseReturnNull() throws ParseException {

        // when
        ZonedDateTime result = zonedDateTimeFormatter.parse("any-text", Locale.ENGLISH);

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldPrintReturnFormattedZonedDateTime() {

        // given
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2019, 6, 1, 15, 14, 30, 123, ZoneId.of("UTC"));

        // when
        String result = zonedDateTimeFormatter.print(zonedDateTime, Locale.ENGLISH);

        // then
        assertThat(result, equalTo("2019. June 01., Saturday, 17:14"));

    }
}
