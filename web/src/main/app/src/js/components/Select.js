import Choices from 'choices.js/assets/scripts/dist/choices';
class Select {
	constructor(){
		this.selector = '.select--choices';
		this.multipleSelector = '.select--choices-multiple';
	}

	init(){
		if( document.querySelector(this.selector) ){
			new Choices(this.selector, {
				itemSelectText: ''
			});
		}

		if( document.querySelector(this.multipleSelector) ){
			new Choices(this.multipleSelector, {
				itemSelectText: '',
				removeItemButton: true,
			});
		}
	}
}

export default Select;