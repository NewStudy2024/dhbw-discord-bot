package dhbw.mos.bot.deadlines;

import dhbw.mos.bot.Common;
import dhbw.mos.bot.config.Config;
import org.jetbrains.annotations.Nullable;

public class DeadlinesService {
    private final Common common;

    public DeadlinesService(Common common) {
        this.common = common;
    }

    public void addDeadline(String name, @Nullable String description, String date) {
        common.getConfigManager().getConfig().getDeadlines().add(new Config.Deadline(System.nanoTime(), name, description, date));
        common.getConfigManager().save();
        updateDeadlines();
    }

    public void deleteDeadlineById(long id) {
        common.getConfigManager().getConfig().getDeadlines().removeIf(deadline -> deadline.getId() == id);
        common.getConfigManager().save();
        updateDeadlines();
    }

    public void updateDeadlines() {
        common.getBackend().updateDeadlines(common.getConfigManager().getConfig().getDeadlines());
    }
}
