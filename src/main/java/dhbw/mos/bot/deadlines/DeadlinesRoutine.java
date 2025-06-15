package dhbw.mos.bot.deadlines;

import dhbw.mos.bot.Common;
import dhbw.mos.bot.Util;

import java.time.Duration;

public class DeadlinesRoutine {
    private final Common common;

    public DeadlinesRoutine(Common common) {
        this.common = common;
    }

    public void initialize() {
        Util.scheduleAtRate("Deadline Update Routine", this::updateDeadlines, Duration.ofSeconds(30));
    }

    private void updateDeadlines() {
        common.getDeadlinesService().updateDeadlines();
    }
}
