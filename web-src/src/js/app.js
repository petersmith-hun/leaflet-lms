import Menu from './components/menu.component';

class App{
    constructor(){
        this.menu = new Menu();
    }

    bootstrap(){
        console.log('App is running.');
        this.menu.init();
    }
}


(new App()).bootstrap();