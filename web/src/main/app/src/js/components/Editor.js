import Calmdown from 'calmdown/src/js/Calmdown';

class Editor {

	constructor() {
		this.configuration = {
			width: '100%',
			height: '300px',
			markdownInputSelector: 'rawContent',
			htmlInputSelector: 'generatedContent'
		};
	}

	init(){
		this.calmdown = new Calmdown(this.configuration);
	}
}

export default Editor;