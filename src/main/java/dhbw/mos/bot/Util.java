package dhbw.mos.bot;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

public class Util {
    public static void scheduleAtRate(String threadName, Runnable task, Duration rate) {
        scheduleAtRate(threadName, task, rate, Duration.ZERO);
    }

    public static void scheduleAtRate(String threadName, Runnable task, Duration rate, Duration delay) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        };

        new Timer(threadName).scheduleAtFixedRate(timerTask, delay.toMillis(), rate.toMillis());
    }
}
