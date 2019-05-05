import copy from 'clipboard-copy';

class LogCopyHandler {

	logCopy(logID) {

		let messageData = {
			level: this.getDetailValue(logID, 'level'),
			source: this.getDetailValue(logID, 'source'),
			timestamp: this.getDetailValue(logID, 'timestamp'),
			thread: this.getDetailValue(logID, 'thread'),
			logger: this.getDetailValue(logID, 'logger'),
			content: this.getDetailValue(logID, 'content'),
			exception_message: this.getDetailValue(logID, 'exception-message'),
			exception_stack_trace: this.getDetailValue(logID, 'exception-stack-trace')
		};

		copy(this.formatLog(messageData));
	}

	getDetailValue(logID, attribute) {

		let detailNode = document.querySelector(logID + ' .log-details-' + attribute);
		let value = null;
		if (detailNode != null) {
			value = detailNode.innerHTML;
		}

		return value;
	}

	formatLog(logData) {

		let formattedData = '';
		for (let property in logData) {
			if (logData.hasOwnProperty(property)) {
				formattedData += '|| ' + property + ' | ' + this.formatProperty(logData, property) + ' |\n';
			}
		}

		return formattedData;
	}

	formatProperty(logData, property) {

		let formattedValue;
		if (logData[property] == null) {
			formattedValue = 'not specified';
		} else if (property === 'content' || property === 'exception_stack_trace') {
			formattedValue = '{code}' + logData[property] + '{code}';
		} else {
			formattedValue = logData[property]
		}

		return formattedValue;
	}
}

class LogCopy {

	constructor() {
		this.selector = '.log-copy';
		this.handler = new LogCopyHandler();
	}

	init() {
		if (document.querySelector(this.selector)) {
			document.querySelectorAll(this.selector).forEach((value) => {
				value.addEventListener('click', () => {
					this.handler.logCopy(value.dataset.id);
					value.classList.add('copied')
				})
			})

		}
	}
}

export default LogCopy;