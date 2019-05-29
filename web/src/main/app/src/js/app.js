// styles
import '../scss/app.scss';
import '../img/default-50x50.gif';
import '../img/android-chrome-192x192.png';
import '../img/android-chrome-512x512.png';
import '../img/apple-touch-icon.png';
import '../img/favicon-16x16.png';
import '../img/favicon-32x32.png';
import '../img/mstile-150x150.png';
// 3rd party
import './vendor'

// components
import CopyImageReference from './components/CopyImageReference';
import DefinitionTableHandler from './components/DefinitionTableHandler';
import LogCopy from './components/LogCopy';
import Logo from './components/Logo';
import Select from './components/Select';
import Editor from './components/Editor';
import ServiceStackStatus from "./components/ServiceStackStatus";

class App {
	constructor(){
		this.copyImageReference = new CopyImageReference();
		this.definitionTableHandler = new DefinitionTableHandler();
		this.logCopy = new LogCopy();
		this.logo = new Logo();
		this.select = new Select();
		this.serviceStackStatus = new ServiceStackStatus();
		this.editor = new Editor();
	}

	init(){
		this.copyImageReference.init();
		this.definitionTableHandler.init();
		this.logCopy.init();
		this.logo.init();
		this.select.init();
		this.serviceStackStatus.init();
		this.editor.init();
	}
}

(new App()).init();