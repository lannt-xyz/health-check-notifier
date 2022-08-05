package xyz.lannt.monitor.schedulingtasks;

import static org.apache.http.HttpStatus.SC_OK;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import xyz.lannt.monitor.notification.Telegram;
import xyz.lannt.monitor.notification.parameter.TelegramMessageParameter;
import xyz.lannt.monitor.properties.HealthCheckProperty;
import xyz.lannt.monitor.properties.TelegramProperty;

@Slf4j
@Component
public class ScheduledTasks {

    @Autowired
    private HealthCheckProperty healthCheckProperty;

    @Autowired
    private TelegramProperty telegramProperty;
    
    private Boolean messageSent = false;

    @Scheduled(fixedRateString = "${health-check.request-interval}", initialDelay = 1000)
    public void monitorHealthCheck() {
        int status = getStatus(healthCheckProperty.getFailThreshold());
        if (SC_OK != status && !messageSent) {
            Telegram telegram = new Telegram(telegramProperty);
            telegram.send(new TelegramMessageParameter());
            messageSent = true;
        }
    }
    
    @Scheduled(fixedRateString = "${health-check.message-sent-duration}", initialDelay = 1000)
    public void resetMessageSent() {
    	messageSent = false;
    }

    private int getStatus(int retryTimes) {
        int status = getStatus();
        if (SC_OK != status && retryTimes > 0) {
            int retrieveCount = healthCheckProperty.getFailThreshold() - retryTimes + 1;
            log.info("health check gone fail, retrying " + retrieveCount);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
            return getStatus(retryTimes - 1);
        }
        return status;
    }

    private int getStatus() {
        HttpClient client = HttpClientBuilder.create().build();
        try {
            HttpResponse response = client.execute(new HttpGet(healthCheckProperty.getUrl()));
            return response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return HttpStatus.SC_BAD_GATEWAY;
    }
}