package hu.psprog.leaflet.lms.web.support.thymeleaf;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link LogMessageRuleProvider}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class LogMessageRuleProviderTest {

    @InjectMocks
    private LogMessageRuleProvider logMessageRuleProvider;

    @ParameterizedTest
    @MethodSource("logMessageRuleProviderDataProvider")
    public void shouldGetRuleReturnRuleName(String logLevel, String expectedRuleName) {

        // when
        String result = logMessageRuleProvider.getRule(logLevel);

        // then
        assertThat(result, equalTo(expectedRuleName));
    }

    private static Stream<Arguments> logMessageRuleProviderDataProvider() {

        return Stream.of(
                Arguments.of("ERROR", "box box-danger"),
                Arguments.of("error", "box box-danger"),
                Arguments.of("WARN", "box box-warning"),
                Arguments.of("warn", "box box-warning"),
                Arguments.of("INFO", "box box-success"),
                Arguments.of("info", "box box-success"),
                Arguments.of("anything-else", "box box-default"),
                Arguments.of(StringUtils.EMPTY, "box box-default"),
                Arguments.of(null, "box box-default")
        );
    }
}
