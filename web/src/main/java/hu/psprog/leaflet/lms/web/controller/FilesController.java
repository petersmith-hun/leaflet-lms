package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.file.DirectoryCreationRequestModel;
import hu.psprog.leaflet.api.rest.request.file.FileUploadRequestModel;
import hu.psprog.leaflet.api.rest.request.file.UpdateFileMetaInfoRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.FileFacade;
import hu.psprog.leaflet.lms.service.facade.impl.utility.URLUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * File management controller for LMS.
 *
 * @author Peter Smith
 */
@Controller
@RequestMapping(FilesController.PATH_FILES)
public class FilesController extends BaseController {

    private static final String VIEW_FILES_UPLOAD_FORM = "view/files/upload_form";
    private static final String VIEW_FILES_BROWSER = "view/files/browser";
    private static final String VIEW_FILES_DETAILS = "view/files/details";
    private static final String VIEW_FILES_EDIT_FORM = "view/files/edit_form";
    private static final String VIEW_FILES_DIR_CREATE_FORM = "view/files/dir_create_form";

    private static final String PATH_FULLY_IDENTIFIED_FILE = "/{id}/{filename:.*}";
    private static final String PATH_RESOURCE = "/resource" + PATH_FULLY_IDENTIFIED_FILE;
    private static final String PATH_BROWSE = "/browse/**";
    private static final String PATH_BROWSE_ROOT = "/browse";
    private static final String PATH_UPLOAD = "/upload/**";
    private static final String PATH_DIRECTORY_CREATE = "/directory/create/**";

    private static final String PATH_VARIABLE_FILENAME = "filename";
    private static final String PATTERN_FILE_BROWSER_ROOT_PATH = "/files/browse/**";

    private static final String FILE_SUCCESSFULLY_UPLOADED = "File successfully uploaded.";
    private static final String FILE_META_INFORMATION_SUCCESSFULLY_UPDATED = "File meta information successfully updated.";
    private static final String DIRECTORY_SUCCESSFULLY_CREATED = "Directory successfully created.";
    private static final String FILE_SUCCESSFULLY_DELETED = "File successfully deleted.";

    static final String PATH_FILES = "/files";

    private FileFacade fileFacade;
    private URLUtilities urlUtilities;

    @Autowired
    public FilesController(FileFacade fileFacade, URLUtilities urlUtilities) {
        this.fileFacade = fileFacade;
        this.urlUtilities = urlUtilities;
    }

    /**
     * Renders file browser interface.
     * Shows existing files and directories stored under given virtual path.
     *
     * @param request {@link HttpServletRequest} object to extract current virtual path
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_BROWSE)
    public ModelAndView listFiles(HttpServletRequest request) throws CommunicationFailureException {

        return modelAndViewFactory
                .createForView(VIEW_FILES_BROWSER)
                .withAttribute("browser", fileFacade.getFilesByFolder(urlUtilities.extractSubPath(PATTERN_FILE_BROWSER_ROOT_PATH, request.getServletPath())))
                .withAttribute("currentURL", urlUtilities.normalize(request.getServletPath()))
                .withAttribute("upURL", urlUtilities.getUpURL(request.getServletPath(), 2))
                .build();
    }

    /**
     * Shows details page of the file identified by given {@link UUID}.
     *
     * @param pathUUID file identifier
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_VIEW)
    public ModelAndView viewFileDetails(@PathVariable(PATH_VARIABLE_ID) UUID pathUUID) throws CommunicationFailureException {

        return modelAndViewFactory
                .createForView(VIEW_FILES_DETAILS)
                .withAttribute("file", fileFacade.getFileDetails(pathUUID))
                .build();

    }

    /**
     * Renders file meta information editor form for the file identified by given {@link UUID}.
     *
     * @param pathUUID file identifier
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_EDIT)
    public ModelAndView showEditFileMetaInfoForm(@PathVariable(PATH_VARIABLE_ID) UUID pathUUID) throws CommunicationFailureException {

        return modelAndViewFactory
                .createForView(VIEW_FILES_EDIT_FORM)
                .withAttribute("file", fileFacade.getFileDetails(pathUUID))
                .build();
    }

    /**
     * Processes file meta information update request.
     *
     * @param pathUUID file identifier
     * @param updateFileMetaInfoRequestModel update meta information
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object - redirection to view file details page
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_EDIT)
    public ModelAndView processEditFileMetaInfo(@PathVariable(PATH_VARIABLE_ID) UUID pathUUID,
                                                @ModelAttribute UpdateFileMetaInfoRequestModel updateFileMetaInfoRequestModel,
                                                RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        fileFacade.processUpdateFileMetaInfo(pathUUID, updateFileMetaInfoRequestModel);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, FILE_META_INFORMATION_SUCCESSFULLY_UPDATED);

        return modelAndViewFactory
                .createRedirectionTo(getRedirectionPath(pathUUID));
    }

    /**
     * Renders file upload form.
     *
     * @return populated {@link ModelAndView} object
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_UPLOAD)
    public ModelAndView showFileUploadForm(HttpServletRequest request) throws CommunicationFailureException {

        String path = urlUtilities.extractSubPath(PATTERN_FILE_BROWSER_ROOT_PATH, request.getServletPath());

        return modelAndViewFactory
                .createForView(VIEW_FILES_UPLOAD_FORM)
                .withAttribute("acceptableMimeTypes", fileFacade.getAcceptableMimeTypes(path).stream()
                        .collect(Collectors.joining(", ")))
                .build();
    }

    /**
     * Processes file upload request.
     *
     * @param fileUploadRequestModel file data
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object - redirection to the view file details page
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_UPLOAD)
    public ModelAndView processFileUpload(@ModelAttribute FileUploadRequestModel fileUploadRequestModel, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        UUID fileID = fileFacade.processFileUpload(fileUploadRequestModel);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, FILE_SUCCESSFULLY_UPLOADED);

        return modelAndViewFactory
                .createRedirectionTo(getRedirectionPath(fileID));
    }

    /**
     * Renders directory creation form.
     *
     * @return populated {@link ModelAndView} object
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_DIRECTORY_CREATE)
    public ModelAndView showCreateDirectoryForm() {

        return modelAndViewFactory
                .createForView(VIEW_FILES_DIR_CREATE_FORM)
                .build();
    }

    /**
     * Processes directory creation request.
     *
     * @param directoryCreationRequestModel directory data
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object - redirection to the file browser
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_DIRECTORY_CREATE)
    public ModelAndView processCreateDirectory(@ModelAttribute DirectoryCreationRequestModel directoryCreationRequestModel,
                                               RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        fileFacade.processCreateDirectory(directoryCreationRequestModel);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, DIRECTORY_SUCCESSFULLY_CREATED);

        return modelAndViewFactory
                .createRedirectionTo(PATH_FILES + PATH_BROWSE_ROOT);
    }

    /**
     * Processes file deletion request.
     *
     * @param pathUUID file identifier
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object - redirection to the file browser
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_DELETE)
    public ModelAndView processDeleteFile(@PathVariable(PATH_VARIABLE_ID) UUID pathUUID, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        fileFacade.processDeleteFile(pathUUID);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, FILE_SUCCESSFULLY_DELETED);

        return modelAndViewFactory
                .createRedirectionTo(PATH_FILES + PATH_BROWSE_ROOT);
    }

    /**
     * Retrieves file identified by given {@link UUID} (and optional filename) transformed to byte array for download.
     *
     * @param pathUUID file identifier
     * @param filename filename (not used for identification)
     * @return file as downloadable resource
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_RESOURCE)
    public ResponseEntity<byte[]> getResource(@PathVariable(PATH_VARIABLE_ID) UUID pathUUID, @PathVariable(PATH_VARIABLE_FILENAME) String filename)
            throws CommunicationFailureException {

        return ResponseEntity
                .ok()
                .body(fileFacade.getFileForDownload(pathUUID, filename));
    }

    private String getRedirectionPath(UUID fileID) {
        return PATH_FILES + replaceIDInViewPath(fileID);
    }
}
