package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.attachment.AttachmentRequestModel;
import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.AttachmentBridgeService;
import hu.psprog.leaflet.lms.service.domain.entry.ModifyEntryRequest;
import hu.psprog.leaflet.lms.service.extraction.CommonExtractor;
import hu.psprog.leaflet.lms.service.util.EntityConnectionDifferenceCalculator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link AttachmentFacadeImpl}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class AttachmentFacadeImplTest {

    private static final UUID PATH_UUID_TO_ATTACH = UUID.randomUUID();
    private static final UUID PATH_UUID_TO_DETACH = UUID.randomUUID();
    private static final ModifyEntryRequest MODIFY_ENTRY_REQUEST = prepareModifyEntryRequest();
    private static final long ENTRY_ID = 1L;
    private static final EditEntryDataModel EDIT_ENTRY_DATA_MODEL = EditEntryDataModel.getExtendedBuilder()
            .withId(ENTRY_ID)
            .withAttachments(Collections.singletonList(FileDataModel.getBuilder()
                    .withReference(PATH_UUID_TO_DETACH.toString())
                    .build()))
            .build();

    @Mock
    private EntityConnectionDifferenceCalculator calculator;

    @Mock
    private AttachmentBridgeService attachmentBridgeService;

    @Mock
    private CommonExtractor commonExtractor;

    @Mock
    private EntityConnectionDifferenceCalculator.EntityConnectionContext<UUID, FileDataModel> context;

    @InjectMocks
    private AttachmentFacadeImpl attachmentFacade;

    @Before
    public void setup() {

        given(calculator.createContextFor(eq(MODIFY_ENTRY_REQUEST.getAttachments()), eq(EDIT_ENTRY_DATA_MODEL.getAttachments()), any()))
                .willReturn(context);
    }

    @Test
    public void shouldHandleAssignmentsWithChanges() throws CommunicationFailureException {

        // given
        given(context.collectForAttach()).willReturn(Collections.singletonList(PATH_UUID_TO_ATTACH));
        given(context.collectForDetach()).willReturn(Collections.singletonList(PATH_UUID_TO_DETACH));

        // when
        attachmentFacade.handleAssignmentsOnChange(MODIFY_ENTRY_REQUEST, EDIT_ENTRY_DATA_MODEL);

        // then
        verify(attachmentBridgeService).attach(prepareAttachmentRequestModel(PATH_UUID_TO_ATTACH));
        verify(attachmentBridgeService).detach(prepareAttachmentRequestModel(PATH_UUID_TO_DETACH));
    }

    private AttachmentRequestModel prepareAttachmentRequestModel(UUID pathUUID) {

        AttachmentRequestModel attachmentRequestModel = new AttachmentRequestModel();
        attachmentRequestModel.setPathUUID(pathUUID);
        attachmentRequestModel.setEntryID(ENTRY_ID);

        return attachmentRequestModel;
    }

    private static ModifyEntryRequest prepareModifyEntryRequest() {

        ModifyEntryRequest request = new ModifyEntryRequest();
        request.setAttachments(Collections.singletonList(PATH_UUID_TO_ATTACH));

        return request;
    }
}