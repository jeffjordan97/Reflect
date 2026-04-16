package com.reflect.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitFilterTest {

    private final RateLimitFilter filter = new RateLimitFilter();

    @Test
    void allowsRequestsWithinLimit() throws Exception {
        for (int i = 0; i < 5; i++) {
            MockHttpServletResponse response = fire("POST", "/api/auth/login", "10.0.0.1");
            assertEquals(200, response.getStatus(), "Request " + (i + 1) + " should succeed");
        }
    }

    @Test
    void rejectsAfterLimitExceeded() throws Exception {
        for (int i = 0; i < 5; i++) {
            fire("POST", "/api/auth/login", "10.0.0.2");
        }
        MockHttpServletResponse response = fire("POST", "/api/auth/login", "10.0.0.2");
        assertEquals(429, response.getStatus());
        assertTrue(response.getContentAsString().contains("Too many requests"));
    }

    @Test
    void doesNotLimitGetRequests() throws Exception {
        for (int i = 0; i < 10; i++) {
            MockHttpServletResponse response = fire("GET", "/api/auth/login", "10.0.0.3");
            assertEquals(200, response.getStatus());
        }
    }

    @Test
    void doesNotLimitNonAuthPaths() throws Exception {
        for (int i = 0; i < 10; i++) {
            MockHttpServletResponse response = fire("POST", "/api/check-ins", "10.0.0.4");
            assertEquals(200, response.getStatus());
        }
    }

    @Test
    void tracksPerIpSeparately() throws Exception {
        for (int i = 0; i < 5; i++) {
            fire("POST", "/api/auth/login", "10.0.0.5");
        }
        // Different IP should still work
        MockHttpServletResponse response = fire("POST", "/api/auth/login", "10.0.0.6");
        assertEquals(200, response.getStatus());
    }

    @Test
    void usesXForwardedForHeader() throws Exception {
        for (int i = 0; i < 5; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
            request.addHeader("X-Forwarded-For", "203.0.113.1, 10.0.0.1");
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(request, response, new MockFilterChain());
        }
        // 6th from same forwarded IP
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.addHeader("X-Forwarded-For", "203.0.113.1, 10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        assertEquals(429, response.getStatus());
    }

    private MockHttpServletResponse fire(String method, String path, String ip) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        request.setRemoteAddr(ip);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response;
    }
}
