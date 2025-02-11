package dhbw.mos.bot.bridge;

import java.util.List;

public interface BotBridge {
    void trackRepository(String owner, String name);
    void untrackRepository(String owner, String name);
    List<String> listTrackedRepositories();
}
