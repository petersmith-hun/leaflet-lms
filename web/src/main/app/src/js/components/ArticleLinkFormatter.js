import removeAccents from "remove-accents";

export default class ArticleLinkFormatter {

	constructor() {
		this.sourceInputID = "#article-title";
		this.targetInputID = "#article-link";
		this.transformButtonID = "#transform-link-from-title";
		this.spacePattern = / /g;
		this.specialCharPattern = /[\[\]()+*!?\\/:;.,]/g
		this.transformingLinkFromTitleEnabled = true;
	}

	init() {
		const sourceInput = document.querySelector(this.sourceInputID);
		const targetInput = document.querySelector(this.targetInputID);
		const transformButton = document.querySelector(this.transformButtonID);

		if (!sourceInput || !targetInput) {
			return;
		}

		this._addListener(sourceInput, targetInput);
		this._handleTransformButton(transformButton, targetInput);
	}

	_addListener(sourceInput, targetInput) {

		sourceInput.addEventListener("keyup", () => {

			if (!this.transformingLinkFromTitleEnabled) {
				return;
			}

			targetInput.value = removeAccents(sourceInput.value)
				.toLowerCase()
				.trim()
				.replace(this.specialCharPattern, '')
				.replace(this.spacePattern, '-');
		});
	}

	_handleTransformButton(transformButton, targetInput) {

		transformButton.addEventListener("click", () => {

			this.transformingLinkFromTitleEnabled = !this.transformingLinkFromTitleEnabled;

			if (this.transformingLinkFromTitleEnabled) {
				transformButton.setAttribute("class", "fa fa-link btn btn-default");
				targetInput.setAttribute("readonly", "readonly");
			} else {
				transformButton.setAttribute("class", "fa fa-unlink btn btn-default");
				targetInput.removeAttribute("readonly");
			}
		});
	}
}
