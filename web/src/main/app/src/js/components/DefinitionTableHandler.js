import jQuery from 'jquery';
import 'admin-lte/bower_components/datatables.net/js/jquery.dataTables';
import 'admin-lte/bower_components/datatables.net-bs/js/dataTables.bootstrap';

class DefinitionTableHandler {

	constructor() {
		this.selector = '#tmsDefinitionTable';
		this.config = {
			'paging'      : true,
			'lengthChange': false,
			'searching'   : true,
			'ordering'    : true,
			'info'        : true,
			'autoWidth'   : true
		};
	}

	init() {
		if (jQuery(this.selector)) {
			jQuery(this.selector).DataTable(this.config);
		}
	}
}

export default DefinitionTableHandler;