export default class Menu{

    constructor({menuClass,dropdownParent,dropdownClass,activeClass} = {}){
        this.menuClass = menuClass || '.main-nav__menu';
        this.dropdownParent = dropdownParent || 'li';
        this.dropdownClass = dropdownClass || '.main-nav__menu-dropdown';
        this.activeClass = activeClass || 'active';
    }

    init(){
        $(this.menuClass).on('click',this.dropdownClass,(e)=>{
            e.preventDefault();
            const $this = $(e.currentTarget);
            $this.closest('li').toggleClass(this.activeClass);
        });
    }
}

