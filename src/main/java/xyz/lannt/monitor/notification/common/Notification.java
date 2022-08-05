package xyz.lannt.monitor.notification.common;

public interface Notification<T extends NotificationProperty> {

    public void send(MessageParameter parameter);
}
