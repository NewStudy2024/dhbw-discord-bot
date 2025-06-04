package dhbw.mos.bot;

import dhbw.mos.bot.cal.Event;
import dhbw.mos.bot.config.ConfigManager;

import java.util.List;

public interface Backend {
    ConfigManager<?> getConfigManager();
    void postDiscussionNotification(String author, String title, String url, Runnable posted);
    void updateCalendarData(List<Event> events);
    void sendCalendarEventNotification();
}
