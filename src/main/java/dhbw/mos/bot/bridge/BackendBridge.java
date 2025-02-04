package dhbw.mos.bot.bridge;

public interface BackendBridge {
    void postDiscussionNotification(String author, String title, String url, Runnable posted);
}
