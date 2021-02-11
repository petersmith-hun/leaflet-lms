import Calmdown from 'calmdown/src/js/Calmdown';
import TextareaEditor from "calmdown/src/js/TextareaEditor";

const _RESOURCE_SERVER_URL_PATTERN = "{resource-server-url}"

class Editor {

	constructor() {
		this.configuration = {
			width: '100%',
			height: '300px',
			markdownInputSelector: 'rawContent',
			htmlInputSelector: 'generatedContent'
		};
	}

	init() {
		if (document.querySelector(".calmdown")) {
			TextareaEditor.prototype.getContent = function () {
				/* eslint-disable no-undef */
				return this.element.value.replace(_RESOURCE_SERVER_URL_PATTERN, calmdownEditorAdditionalConfig.resourceServerUrl);
			}
			this.calmdown = new Calmdown(this.configuration);
		}
	}
}

export default Editor;