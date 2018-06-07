import Vivus from 'vivus';

class Logo{

	constructor(){
		this.logo = 'leaflet-logo';
		this.conf = {
			duration: 500,
			pathTimingFunction: Vivus.EASE
		};
	}

	init(){
		this.leafletLogoVivus = new Vivus(this.logo,this.conf);
	}
}

export default Logo;