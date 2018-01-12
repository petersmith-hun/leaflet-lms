package hu.psprog.leaflet.lms.web.auth.device;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link DeviceIDGenerator}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceIDGeneratorTest {

    @InjectMocks
    private DeviceIDGenerator deviceIDGenerator;

    @Test
    public void shouldReturnNonNullDeviceID() {

        // given
        deviceIDGenerator.setup(); // should be automatic on bean creation and only on creation

        // when
        UUID result = deviceIDGenerator.getID();

        // then
        assertThat(result, notNullValue());
    }

    @Test
    public void shouldAlwaysReturnDifferentDeviceID() {

        // given
        int numberOfGeneratedDeviceIDs = 10;
        Set<UUID> generatedUUIDs = new HashSet<>();

        // when
        for (int counter = 0; counter < numberOfGeneratedDeviceIDs; counter++) {
            deviceIDGenerator.setup();
            generatedUUIDs.add(deviceIDGenerator.getID());
        }

        // then
        assertThat(generatedUUIDs.size(), equalTo(numberOfGeneratedDeviceIDs));
    }
}