open module leaflet.app.lms.web {
    requires leaflet.app.lms.service;
    requires leaflet.component.bridge.api;
    requires leaflet.component.bridge.implementation;
    requires leaflet.component.rest.backend.api;
    requires leaflet.component.rest.failover.api;
    requires transitive leaflet.component.rest.support.hystrix;
    requires leaflet.component.rest.tlp.api;
    requires leaflet.component.rest.tms.api;
    requires leaflet.component.tlp.appender;
    requires leaflet.component.tms.adapter;

    requires java.compiler;
    requires com.fasterxml.jackson.databind;
    requires org.apache.commons.lang3;
    requires slf4j.api;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.boot.configuration.processor;
    requires spring.context;
    requires spring.core;
    requires spring.security.config;
    requires spring.security.core;
    requires spring.security.web;
    requires spring.web;
    requires spring.webmvc;
    requires tomcat.embed.core;
}