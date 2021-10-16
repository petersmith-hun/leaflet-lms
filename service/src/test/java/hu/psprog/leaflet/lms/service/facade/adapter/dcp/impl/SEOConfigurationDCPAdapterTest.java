package hu.psprog.leaflet.lms.service.facade.adapter.dcp.impl;

import hu.psprog.leaflet.api.rest.request.dcp.DCPRequestModel;
import hu.psprog.leaflet.api.rest.response.dcp.DCPDataModel;
import hu.psprog.leaflet.api.rest.response.dcp.DCPListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.DCPStoreBridgeService;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link SEOConfigurationDCPAdapter}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class SEOConfigurationDCPAdapterTest {

    private static final String KEY_PAGE_TITLE = "PAGE_TITLE";
    private static final String KEY_META_TITLE = "META_TITLE";
    private static final String KEY_META_DESCRIPTION = "META_DESCRIPTION";
    private static final String KEY_META_KEYWORDS = "META_KEYWORDS";
    private static final String UNRELATED_KEY = "UNRELATED_KEY";
    private static final String UNRELATED_KEY_2 = "UNRELATED_KEY_2";

    private static final String VALUE_PAGE_TITLE = "Page title";
    private static final String VALUE_META_TITLE = "Meta title";
    private static final String VALUE_META_DESCRIPTION = "Meta description";
    private static final String VALUE_META_KEYWORDS = "Meta keywords";
    private static final String UNRELATED_VALUE = "Unrelated value";
    private static final String UNRELATED_VALUE_2 = "Unrelated value 2";

    @Mock
    private DCPStoreBridgeService dcpStoreBridgeService;

    @InjectMocks
    private SEOConfigurationDCPAdapter adapter;

    @Test
    public void shouldCollectToDomain() throws CommunicationFailureException {

        // given
        given(dcpStoreBridgeService.getAllDCPEntries()).willReturn(prepareDCPListDataModelForCollect());

        // when
        SEOConfiguration result = adapter.collect();

        // then
        assertThat(result, notNullValue());
        assertThat(result.getPageTitle(), equalTo(VALUE_PAGE_TITLE));
        assertThat(result.getDefaultDescription(), equalTo(VALUE_META_DESCRIPTION));
        assertThat(result.getDefaultKeywords(), equalTo(VALUE_META_KEYWORDS));
        assertThat(result.getDefaultTitle(), equalTo(VALUE_META_TITLE));
    }

    @Test
    public void shouldReturnEmptySEOConfigurationForEmptyDCP() throws CommunicationFailureException {

        // given
        given(dcpStoreBridgeService.getAllDCPEntries()).willReturn(DCPListDataModel.getBuilder().build());

        // when
        SEOConfiguration result = adapter.collect();

        // then
        assertThat(result, notNullValue());
        assertThat(result, equalTo(new SEOConfiguration()));
    }

    @Test
    public void shouldUpdate() throws CommunicationFailureException {

        // given
        SEOConfiguration seoConfiguration = SEOConfiguration.getBuilder()
                .withPageTitle(VALUE_PAGE_TITLE)
                .withDefaultDescription(VALUE_META_DESCRIPTION)
                .withDefaultKeywords(VALUE_META_KEYWORDS)
                .withDefaultTitle(VALUE_META_TITLE)
                .build();
        given(dcpStoreBridgeService.getAllDCPEntries()).willReturn(prepareDCPListDataModelForUpdate());

        // when
        adapter.update(seoConfiguration);

        // then
        verifyUpdateCall(KEY_PAGE_TITLE, VALUE_PAGE_TITLE);
        verifyUpdateCall(KEY_META_TITLE, VALUE_META_TITLE);
        verifyCreateCall(KEY_META_DESCRIPTION, VALUE_META_DESCRIPTION);
        verifyCreateCall(KEY_META_KEYWORDS, VALUE_META_KEYWORDS);
    }

    private void verifyCreateCall(String key, String value) throws CommunicationFailureException {
        verify(dcpStoreBridgeService).createDCPEntry(prepareDCPRequestModel(key, value));
    }

    private void verifyUpdateCall(String key, String value) throws CommunicationFailureException {
        verify(dcpStoreBridgeService).updateDCPEntry(prepareDCPRequestModel(key, value));
    }

    private DCPRequestModel prepareDCPRequestModel(String key, String value) {

        DCPRequestModel dcpRequestModel = new DCPRequestModel();
        dcpRequestModel.setKey(key);
        dcpRequestModel.setValue(value);

        return dcpRequestModel;
    }

    private DCPListDataModel prepareDCPListDataModelForUpdate() {
        return DCPListDataModel.getBuilder()
                .withItem(prepareDCPDataModel(UNRELATED_KEY, UNRELATED_VALUE))
                .withItem(prepareDCPDataModel(KEY_PAGE_TITLE, VALUE_PAGE_TITLE))
                .withItem(prepareDCPDataModel(KEY_META_TITLE, VALUE_META_TITLE))
                .build();
    }

    private DCPListDataModel prepareDCPListDataModelForCollect() {
        return DCPListDataModel.getBuilder()
                .withItem(prepareDCPDataModel(UNRELATED_KEY, UNRELATED_VALUE))
                .withItem(prepareDCPDataModel(KEY_PAGE_TITLE, VALUE_PAGE_TITLE))
                .withItem(prepareDCPDataModel(KEY_META_TITLE, VALUE_META_TITLE))
                .withItem(prepareDCPDataModel(KEY_META_DESCRIPTION, VALUE_META_DESCRIPTION))
                .withItem(prepareDCPDataModel(KEY_META_KEYWORDS, VALUE_META_KEYWORDS))
                .withItem(prepareDCPDataModel(UNRELATED_KEY_2, UNRELATED_VALUE_2))
                .build();
    }
    
    private DCPDataModel prepareDCPDataModel(String key, String value) {
        return DCPDataModel.getBuilder()
                .withKey(key)
                .withValue(value)
                .build();
    }
}