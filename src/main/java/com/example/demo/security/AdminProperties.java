package com.example.demo.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The single professor/administrator authenticates with a fixed API key
 * (see requirement 11 - "Professor / Administrador"), supplied via the
 * ADMIN_API_KEY environment variable. This is intentionally simple for a
 * first version with a single admin; {@link com.example.demo.security.AdminApiKeyFilter}
 * is the only place this key is checked, so swapping in a real
 * authentication mechanism later does not require touching every
 * admin controller.
 */
@ConfigurationProperties(prefix = "app.admin")
public class AdminProperties {

    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
