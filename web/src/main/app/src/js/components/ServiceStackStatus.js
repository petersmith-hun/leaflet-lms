import {EventSourcePolyfill} from 'ng-event-source';
import {Observable} from "rxjs";

class ServiceStackStatus {

	constructor() {
		this.selector = "#dashboard";
		this.eventSourceInitDict = {
			https: {
				rejectUnauthorized: false
			}
		};
		/* eslint-disable no-undef */
		this.stackStatusConfig = typeof stackStatusConfig === 'undefined'
			? {enabled: false}
			: stackStatusConfig;
	}

	init() {

		if (!this.stackStatusConfig.enabled) {
			return;
		}

		let dashboard = document.querySelector(this.selector);
		if (dashboard && this.stackStatusConfig.enabled) {
			this.createStatusObservable().subscribe(status => {
				dashboard.querySelector(`#svcStatus_${status.app.abbreviation}`)
					.outerHTML = this.getStatusBox(status);
			});
		}
	}

	createStatusObservable() {
		return new Observable((observer) => {

			let eventSource = new EventSourcePolyfill(this.stackStatusConfig.discoverEndpoint, this.eventSourceInitDict);
			eventSource.onmessage = (event) => {
				let value = JSON.parse(event.data);
				observer.next(value)
			};

			eventSource.onerror = () => {
				if (eventSource.readyState === 0) {
					eventSource.close();
					observer.complete();
				}
			};
		});
	}

	getStatusBox(status) {
		return status.up
			? this.createSuccessfulStatusBox(status)
			: this.createFailureStatusBox(status);
	}

	createSuccessfulStatusBox(status) {
		return `
			<div class="col-md-3">
				<div class="box box-success">
					<div class="box-header">
						<h3 class="box-title">${status.app.abbreviation}</h3>								
					</div>
					<div class="box-body">
						<p>${status.app.name}</p>
						<p>${status.build.version} [built on ${status.build.time}] running</p>
					</div>
				</div>
			</div>`;
	}

	createFailureStatusBox(status) {
		return `
			<div class="col-md-3">
				<div class="box box-danger">
					<div class="box-header">
						<h3 class="box-title">${status.app.abbreviation}</h3>								
					</div>
					<div class="box-body">
						<p>Not responding</p>
					</div>
				</div>
			</div>`;
	}
}

export default ServiceStackStatus;
