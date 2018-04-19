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
import Logo from './components/logo';

class App {
	constructor(){
		this.logo = new Logo;
	}

	init(){
		this.logo.init();
	}
}

(new App).init();