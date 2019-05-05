package hu.psprog.leaflet.lms.web.support.thymeleaf;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link LogMessageRuleProvider}.
 *
 * @author Peter Smith
 */
@RunWith(JUnitParamsRunner.class)
public class LogMessageRuleProviderTest {

    private LogMessageRuleProvider logMessageRuleProvider;

    @Before
    public void setup() {
        logMessageRuleProvider = new LogMessageRuleProvider();
    }

    @Test
    @Parameters(source = LogMessageRuleProviderDataSource.class)
    public void shouldGetRuleReturnRuleName(String logLevel, String expectedRuleName) {

        // when
        String result = logMessageRuleProvider.getRule(logLevel);

        // then
        assertThat(result, equalTo(expectedRuleName));
    }

    public static class LogMessageRuleProviderDataSource {

        public static Object[] provide() {
            return new Object[] {
                    new Object[] {"ERROR", "box box-danger"},
                    new Object[] {"error", "box box-danger"},
                    new Object[] {"WARN", "box box-warning"},
                    new Object[] {"warn", "box box-warning"},
                    new Object[] {"INFO", "box box-success"},
                    new Object[] {"info", "box box-success"},
                    new Object[] {"anything-else", "box box-default"},
                    new Object[] {StringUtils.EMPTY, "box box-default"},
                    new Object[] {null, "box box-default"},
            };
        }
    }
}
