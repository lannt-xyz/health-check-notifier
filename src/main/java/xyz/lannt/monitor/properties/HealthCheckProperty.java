package xyz.lannt.monitor.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "health-check")
public class HealthCheckProperty {

    private String url;
    private Integer requestInterval;
    private Integer failThreshold;
}
