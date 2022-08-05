package xyz.lannt.monitor.notification;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.lannt.monitor.notification.common.MessageParameter;
import xyz.lannt.monitor.notification.common.Notification;
import xyz.lannt.monitor.properties.TelegramProperty;

@Slf4j
@AllArgsConstructor
public class Telegram implements Notification<TelegramProperty> {

    private TelegramProperty properties;

    @Override
    public void send(MessageParameter parameter) {
        HttpClient client = HttpClientBuilder.create().build();
        try {
            String message = String.format(properties.getMessageTemplate(),
                    LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            String url = String.format(properties.getUrl(), urlEncode(properties.getToken()), properties.getChatId(),
                    urlEncode(message));
            client.execute(new HttpGet(url));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String urlEncode(String urlPart) {
        try {
            return URLEncoder.encode(urlPart, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error when encode for URL");
        }
    }
}
