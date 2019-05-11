import copy from 'clipboard-copy';

class CopyImageReference {

	constructor() {
		this.selector = '#copyFileReference';
	}

	init() {

		let referenceCopyButton = document.querySelector(this.selector);
		if (referenceCopyButton) {
			referenceCopyButton.addEventListener('click', () => {
				copy(this.extractReference(referenceCopyButton));

				referenceCopyButton.classList.remove('btn-primary')
				referenceCopyButton.classList.add('btn-success');
				referenceCopyButton.innerHTML = '<i class="fa fa-check"></i> Copied';
			});
		}
	}

	extractReference(buttonNode) {

		let formattedReference = '';
		let reference = buttonNode.dataset.reference;
		if (reference !== null) {
			formattedReference = "![alt]({resource-server-url}" + reference + ")";
		}

		return formattedReference;
	}
}

export default CopyImageReference;
