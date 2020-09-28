import {EventSourcePolyfill} from 'ng-event-source';
import {Observable} from "rxjs";
import "jquery-knob";
import moment from "moment";
import momentDurationFormatSetup from "moment-duration-format";

class DockerClusterStatus {

	constructor() {
		this.selector = ".docker-status-card";
		this.knobConfig = {
			readOnly: true,
			width: 90,
			height: 90,
			format: value => `${value}%`
		};
		/* eslint-disable no-undef */
		this.dockerClusterStatusConfig = typeof dockerClusterStatusConfig === 'undefined'
			? {enabled: false, apiKey: null}
			: dockerClusterStatusConfig;
		this.eventSourceInitDict = {
			https: {
				rejectUnauthorized: false
			},
			headers: {
				"X-Api-Key": this.dockerClusterStatusConfig.apiKey
			}
		};
	}

	init() {

		if (!this.dockerClusterStatusConfig.enabled) {
			return;
		}

		momentDurationFormatSetup(moment);

		setTimeout(() => {
			const containerIDList = this._extractContainerIDList();
			this._createStatusObservable(containerIDList);
			this._createDetailsObservable(containerIDList);
			this._setTotalMemoryUsage();
			this._checkStoppedContainers();
		}, 300);
	}

	_extractContainerIDList() {

		const statusCards = document.querySelectorAll(this.selector);
		statusCards.forEach(statusCard => {
			const containerID = statusCard.dataset.container;
			const cpuIndicatorID = "#cpu_" + containerID;
			const memoryIndicatorID = "#mem_" + containerID;

			$(cpuIndicatorID).knob(this.knobConfig);
			$(memoryIndicatorID).knob(this.knobConfig);
		});

		return Array.from(statusCards.values())
			.map(card => card.dataset.container)
			.reduce((previousValue, currentValue) => `${previousValue},${currentValue}`);
	}

	_createStatusObservable(containerIDList) {

		this._createObservable(this.dockerClusterStatusConfig.statusEndpoint, containerIDList, status => {

			const containerID = status.id;
			const cpuIndicatorID = "#cpu_" + containerID;
			const memoryIndicatorID = "#mem_" + containerID;
			const memoryInMBIndicatorID = "#mem_mb_" + containerID;

			$(cpuIndicatorID)
				.val(Math.round(status.cpuUsagePercent))
				.trigger("change");

			const percentage = Math.round(status.memoryUsagePercent);
			$(memoryIndicatorID)
				.val(percentage)
				.trigger("change");

			$(memoryInMBIndicatorID).html(`${status.memoryUsageInMegabytes} MB`);
			$(memoryInMBIndicatorID).attr("data-memory", status.memoryUsageInMegabytes);
			$(memoryIndicatorID).attr("data-memory-percentage", percentage);
		});
	}

	_createDetailsObservable(containerIDList) {

		this._createObservable(this.dockerClusterStatusConfig.detailsEndpoint, containerIDList, details => {

			const containerID = details.id;
			const card = "#container_" + containerID;
			const uptimeIndicatorID = "#uptime_" + containerID;
			const logsIndicatorID = "#logs_" + containerID;
			const stateIndicatorID = "#state_" + containerID;
			const startedAt = new Date(details.startedAt);

			if (details.status === "RUNNING") {
				let uptime = parseInt((new Date() - startedAt) / 1000);
				$(uptimeIndicatorID).html(moment.duration(uptime, "seconds").format("D [days], HH [hours], mm [minutes], ss [seconds]"));
				$(card).removeClass("box-danger");
				$(card).addClass("box-success");
			} else {
				$(uptimeIndicatorID).html("Container is stopped");
				$(card).removeClass("box-success");
				$(card).addClass("box-danger");
			}
			$(logsIndicatorID).html(details.logPath);
			$(stateIndicatorID).html(details.status.toLowerCase());
		});
	}

	_createObservable(path, containerIDList, nextCallback) {
		new Observable((observer) => {

			let eventSource = new EventSourcePolyfill(path.replace("{ids}", containerIDList), this.eventSourceInitDict);
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
		}).subscribe(nextCallback);
	}

	_setTotalMemoryUsage() {

		setInterval(() => {

			const totalMemoryUsageInMB = Array.from(document.querySelectorAll(".mem-mb-indicator").values())
				.map(node => node.dataset.memory)
				.reduce((previousValue, currentValue) => parseInt(previousValue) + parseInt(currentValue), 0);

			const totalMemoryUsagePercentage = Array.from(document.querySelectorAll(".mem-percentage-indicator").values())
				.map(node => parseInt(node.dataset.memoryPercentage))
				.reduce((previousValue, currentValue) => previousValue + currentValue, 0);

			$("#mem_total_mb").html(`${totalMemoryUsageInMB} MB`);
			$("#mem_usage_progress_bar")
				.css("width", `${totalMemoryUsagePercentage}%`)
				.attr("aria-valuenow", totalMemoryUsagePercentage);
		}, 3000);
	}

	_checkStoppedContainers() {

		const notificationBoxID = "#stopped_containers_notification";
		const stoppedContainerCountSpanID = "#stopped_containers_count";

		setInterval(() => {
			const numberOfStoppedContainers = Array.from(document.querySelectorAll(".state-indicator").values())
				.map(node => node.innerHTML)
				.filter(value => value.toLowerCase() !== "running")
				.length;

			if (numberOfStoppedContainers > 0) {
				$(stoppedContainerCountSpanID).html(numberOfStoppedContainers);
				$(notificationBoxID).css("visibility", "visible");
			} else {
				$(stoppedContainerCountSpanID).html(0);
				$(notificationBoxID).css("visibility", "hidden");
			}
		}, 5000);
	}
}

export default DockerClusterStatus;
