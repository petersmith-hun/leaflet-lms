open module leaflet.app.lms.service {
    requires leaflet.component.bridge.api;
    requires leaflet.component.bridge.integration.spring;
    requires leaflet.component.rest.backend.api;
    requires leaflet.component.rest.backend.client;
    requires leaflet.component.rest.failover.api;
    requires leaflet.component.rest.failover.client;
    requires leaflet.component.rest.request.adapters;
    requires leaflet.component.rest.tlp.api;
    requires leaflet.component.rest.tlp.client;
    requires leaflet.component.rest.tms.api;
    requires leaflet.component.rest.tms.client;
    requires leaflet.component.security.jwt.support.api;
    requires leaflet.component.security.jwt.support.handler;

    requires java.validation;
    requires java.ws.rs;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires org.apache.commons.collections4;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.jvnet.mimepull;
    requires org.slf4j;
    requires spring.beans;
    requires spring.boot;
    requires spring.context;
    requires spring.core;
    requires spring.security.core;
    requires spring.web;

    exports hu.psprog.leaflet.lms.service.domain.common;
    exports hu.psprog.leaflet.lms.service.domain.entry;
    exports hu.psprog.leaflet.lms.service.domain.file;
    exports hu.psprog.leaflet.lms.service.domain.role;
    exports hu.psprog.leaflet.lms.service.domain.system;
    exports hu.psprog.leaflet.lms.service.domain.translations;
    exports hu.psprog.leaflet.lms.service.facade;
    exports hu.psprog.leaflet.lms.service.facade.impl.utility;
}