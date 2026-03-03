package com.wholeyear.solver;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * Forwards any non-API, non-static-asset request to index.html so that
 * the React SPA can handle client-side routing.
 *
 * When a request hits a path that doesn't map to a controller or static
 * resource, the embedded Tomcat returns a 404. This error page registration
 * redirects that 404 to /index.html, letting React handle the route.
 */
@Configuration
public class WebConfig {

    @Bean
    public ErrorPageRegistrar errorPageRegistrar() {
        return registry -> registry.addErrorPages(
                new ErrorPage(HttpStatus.NOT_FOUND, "/index.html"));
    }
}
