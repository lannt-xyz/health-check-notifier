package xyz.lannt.monitor.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import xyz.lannt.monitor.notification.common.NotificationProperty;

@Data
@Component
@ConfigurationProperties(prefix = "telegram")
public class TelegramProperty implements NotificationProperty {

    private String url;
    private String token;
    private String chatId;
    private String messageTemplate;
}
