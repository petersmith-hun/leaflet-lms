package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.document.DocumentCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.document.DocumentUpdateRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.DocumentFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Document management controller for LMS.
 *
 * @author Peter Smith
 */
@Controller
@RequestMapping(DocumentsController.PATH_DOCUMENTS)
public class DocumentsController extends BaseController {

    private static final String VIEW_DOCUMENTS_LIST = "view/documents/list";
    private static final String VIEW_DOCUMENTS_DETAILS = "view/documents/details";
    private static final String VIEW_DOCUMENTS_EDIT_FORM = "view/documents/edit_form";

    private static final String DOCUMENT_SUCCESSFULLY_CREATED = "Document successfully created.";
    private static final String DOCUMENT_SUCCESSFULLY_UPDATED = "Document successfully updated.";
    private static final String DOCUMENT_SUCCESSFULLY_DELETED = "Document successfully deleted.";
    private static final String DOCUMENT_STATUS_SUCCESSFULLY_CHANGED = "Document status successfully changed to %s";

    static final String PATH_DOCUMENTS = "/documents";
    private static final String PATH_CREATE_DOCUMENT = PATH_DOCUMENTS + PATH_CREATE;

    private DocumentFacade documentFacade;

    @Autowired
    public DocumentsController(DocumentFacade documentFacade) {
        this.documentFacade = documentFacade;
    }

    /**
     * Returns documents for management interface.
     *
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listDocuments() throws CommunicationFailureException {

        return modelAndViewFactory
                .createForView(VIEW_DOCUMENTS_LIST)
                .withAttribute("documents", documentFacade.getAllDocuments())
                .build();
    }

    /**
     * Shows document details.
     *
     * @param documentID ID of the document to retrieve details for
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_VIEW)
    public ModelAndView viewDocument(@PathVariable(PATH_VARIABLE_ID) Long documentID) throws CommunicationFailureException {

        return modelAndViewFactory
                .createForView(VIEW_DOCUMENTS_DETAILS)
                .withAttribute("document", documentFacade.getDocument(documentID))
                .build();
    }

    /**
     * Renders document creation form.
     *
     * @return populated {@link ModelAndView} object
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_CREATE)
    public ModelAndView showCreateDocumentForm() {

        return modelAndViewFactory
                .createForView(VIEW_DOCUMENTS_EDIT_FORM)
                .build();
    }

    /**
     * Processes document creation request.
     *
     * @param documentCreateRequestModel document data
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object (redirection to view the created document)
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_CREATE)
    public ModelAndView processCreateDocument(@ModelAttribute DocumentCreateRequestModel documentCreateRequestModel, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        return handleValidationFailure(() -> {
            documentCreateRequestModel.setUserID(currentUserID());
            Long createdDocumentID = documentFacade.processCreateDocument(documentCreateRequestModel);
            redirectAttributes.addFlashAttribute(FLASH_MESSAGE, DOCUMENT_SUCCESSFULLY_CREATED);

            return modelAndViewFactory.createRedirectionTo(getRedirectionPath(createdDocumentID));
        }, validationFailureRedirectionSupplier(redirectAttributes, documentCreateRequestModel, PATH_CREATE_DOCUMENT));
    }

    /**
     * Renders document edit form.
     *
     * @param documentID ID of the document to be edited
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_EDIT)
    public ModelAndView showEditDocumentForm(@PathVariable(PATH_VARIABLE_ID) Long documentID) throws CommunicationFailureException {

        return modelAndViewFactory
                .createForView(VIEW_DOCUMENTS_EDIT_FORM)
                .withAttribute("document", documentFacade.getDocument(documentID))
                .build();
    }

    /**
     * Processed document edit request.
     *
     * @param documentID ID of the document to be edited
     * @param documentUpdateRequestModel updated document data
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object (redirection to view the edited document)
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_EDIT)
    public ModelAndView processEditDocument(@PathVariable(PATH_VARIABLE_ID) Long documentID,
                                            @ModelAttribute DocumentUpdateRequestModel documentUpdateRequestModel,
                                            RedirectAttributes redirectAttributes) throws CommunicationFailureException {

        return handleValidationFailure(() -> {
            documentFacade.processEditDocument(documentID, documentUpdateRequestModel);
            redirectAttributes.addFlashAttribute(FLASH_MESSAGE, DOCUMENT_SUCCESSFULLY_UPDATED);

            return modelAndViewFactory.createRedirectionTo(getRedirectionPath(documentID));
        }, validationFailureRedirectionSupplier(redirectAttributes, documentUpdateRequestModel, getRedirectionPath(documentID)));
    }

    /**
     * Processes document deletion request.
     *
     * @param documentID ID of the document to be deleted
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object (redirection to document list)
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_DELETE)
    public ModelAndView processDeleteDocument(@PathVariable(PATH_VARIABLE_ID) Long documentID, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        documentFacade.processDeleteDocument(documentID);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, DOCUMENT_SUCCESSFULLY_DELETED);

        return modelAndViewFactory
                .createRedirectionTo(PATH_DOCUMENTS);
    }

    /**
     * Processes document status change request.
     *
     * @param documentID ID of the document the status to be changed of
     * @param redirectTo value where redirect after the change to
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object (redirection to view the edited document)
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_STATUS)
    public ModelAndView processChangeDocumentStatus(@PathVariable(PATH_VARIABLE_ID) Long documentID,
                                                    @RequestParam(REQUEST_PARAM_REDIRECT) String redirectTo,
                                                    RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        String currentStatus = documentFacade.processChangeDocumentStatus(documentID)
                ? "enabled"
                : "disabled";
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, String.format(DOCUMENT_STATUS_SUCCESSFULLY_CHANGED, currentStatus));

        return modelAndViewFactory
                .createRedirectionTo(getRedirectionPath(documentID, redirectTo));
    }

    private String getRedirectionPath(Long documentID, String redirectTo) {
        return redirectTo.equalsIgnoreCase("list")
                ? PATH_DOCUMENTS
                : getRedirectionPath(documentID);
    }

    private String getRedirectionPath(Long documentID) {
        return PATH_DOCUMENTS + replaceIDInViewPath(documentID);
    }
}
