package com.pharm.pharmfinder.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * CORS Configuration
 */
@Slf4j
public class RapidCorsConfigurationSource implements CorsConfigurationSource {


    private Cors cors;

    public RapidCorsConfigurationSource() {
        this.cors = new Cors();

    }

    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedHeaders(Arrays.asList(cors.getAllowedHeaders()));
        config.setAllowedMethods(Arrays.asList(cors.getAllowedMethods()));
        config.setAllowedOrigins(Arrays.asList(cors.getAllowedOrigins()));
        config.setExposedHeaders(Arrays.asList(cors.getExposedHeaders()));
        config.setMaxAge(cors.getMaxAge());

        return config;
    }

    /**
     * CORS configuration related properties
     */
    @Getter
    @Setter
    private static class Cors {

        /**
         * Comma separated whitelisted URLs for CORS.
         * Should contain the applicationURL at the minimum.
         * Not providing this property would disable CORS configuration.
         */
        public String[] allowedOrigins;

        /**
         * Methods to be allowed, e.g. GET,POST,...
         */
        public String[] allowedMethods = {"GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "OPTIONS", "PATCH"};

        /**
         * Request headers to be allowed, e.g. content-type,accept,origin,x-requested-with,...
         */
        public String[] allowedHeaders = {
                "Accept",
                "Accept-Encoding",
                "Accept-Language",
                "Cache-Control",
                "Connection",
                "Content-Length",
                "Content-Type",
                "Cookie",
                "Host",
                "Origin",
                "Pragma",
                "Referer",
                "User-Agent",
                "x-requested-with",
                HttpHeaders.AUTHORIZATION};

        /**
         * Response headers that you want to expose to the client JavaScript programmer, e.g. Lemon-Authorization.
         * I don't think we need to mention here the headers that we don't want to access through JavaScript.
         * Still, by default, we have provided most of the common headers.
         *
         * <br>
         * See <a href="http://stackoverflow.com/questions/25673089/why-is-access-control-expose-headers-needed#answer-25673446">
         * here</a> to know why this could be needed.
         */
        public String[] exposedHeaders = {
                "Cache-Control",
                "Connection",
                "Content-Type",
                "Date",
                "Expires",
                "Pragma",
                "Server",
                "Set-Cookie",
                "Transfer-Encoding",
                "X-Content-Type-Options",
                "X-XSS-Protection",
                "X-Frame-Options",
                "X-Application-Context",
                HttpHeaders.AUTHORIZATION};

        /**
         * CORS <code>maxAge</code> long property
         */
        public long maxAge = 3600L;

    }

}
