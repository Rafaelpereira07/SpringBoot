package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Backs the {@code app.artificial-delay.*} properties so the simulated
 * network latency (see {@link ArtificialDelayFilter}) can be toggled or
 * tuned per environment without touching code.
 */
@ConfigurationProperties(prefix = "app.artificial-delay")
public class ArtificialDelayProperties {

    /** Whether the artificial delay filter is active. Disabled in tests. */
    private boolean enabled = true;

    /** Delay applied to each request, in milliseconds. */
    private long milliseconds = 500;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }
}
