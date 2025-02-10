package dhbw.mos.bot.bridge;

import dhbw.mos.bot.cal.Event;

import java.util.List;

public interface BackendBridge {
    void postDiscussionNotification(String author, String title, String url, Runnable posted);
    void updateCalendarData(List<Event> events);
    void sendCalendarEventNotification();
}
