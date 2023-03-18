import Calmdown from 'calmdown/src/js/Calmdown';
import ShowdownConverter from "calmdown/src/js/ShowdownConverter";

const _RESOURCE_SERVER_URL_PATTERN = /{resource-server-url}/g

class Editor {

	constructor() {
		this.configuration = {
			width: '100%',
			height: '700px',
			markdownInputSelector: 'rawContent',
			htmlInputSelector: 'generatedContent'
		};
	}

	init() {
		if (document.querySelector(".calmdown")) {
			ShowdownConverter.prototype.makeHtml = function (markdown) {
				/* eslint-disable no-undef */
				const updatedMarkdown = markdown.replace(_RESOURCE_SERVER_URL_PATTERN, calmdownEditorAdditionalConfig.resourceServerUrl);
				const html = this.converter.makeHtml(updatedMarkdown);

				return this.codeHighlighter == null ? html : this.addCodeHighlighting(html);
			}
			this.calmdown = new Calmdown(this.configuration);
		}
	}
}

export default Editor;