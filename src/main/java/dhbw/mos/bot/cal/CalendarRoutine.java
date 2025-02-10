package dhbw.mos.bot.cal;

import dhbw.mos.bot.Bot;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CalendarRoutine {
    private static final Duration NOTIFICATION_OFFSET = Duration.ofMinutes(5);

    private final Bot bot;
    private Instant lastNotified = Instant.EPOCH;


    public CalendarRoutine(Bot bot) {
        this.bot = bot;

        TimerTask updateTask = new TimerTask() {
            @Override
            public void run() {
                CalendarRoutine.this.updateCalendar();
            }
        };

        TimerTask notificationTask = new TimerTask() {
            @Override
            public void run() {
                CalendarRoutine.this.sendEventNotifications();
            }
        };

        new Timer("Calendar update routine")
                .scheduleAtFixedRate(updateTask, 1000, Duration.ofSeconds(60).toMillis());
        new Timer("Calendar notification routine")
                .scheduleAtFixedRate(notificationTask, 5000, Duration.ofSeconds(5).toMillis());
    }

    private void updateCalendar() {
        bot.getCalendarService().reload();

        ZonedDateTime todayStart = bot.getCalendarService().getNow().withHour(0).withMinute(0).withSecond(0);
        ZonedDateTime todayEnd = todayStart.plusDays(1);


        List<Event> eventsToday = bot.getCalendarService().getEvents()
                .stream()
                .filter(event -> doesTimeOverlap(todayStart, todayEnd, event.start(), event.end()))
                .toList();

        bot.getBackend().ifPresent(backend -> backend.updateCalendarData(eventsToday));
    }

    private void sendEventNotifications() {
        ZonedDateTime now = bot.getCalendarService().getNow();

        if (lastNotified.plus(NOTIFICATION_OFFSET).isAfter(now.toInstant())) return;

        bot.getCalendarService().getEvents().stream()
                .filter(event -> event.start().isAfter(now))
                .filter(event -> event.start().isBefore(now.plus(NOTIFICATION_OFFSET)))
                .findFirst()
                .flatMap(event -> bot.getBackend())
                .ifPresent(backend -> {
                    backend.sendCalendarEventNotification();
                    lastNotified = Instant.now();
                });
    }

    private boolean doesTimeOverlap(ZonedDateTime aStart, ZonedDateTime aEnd, ZonedDateTime bStart, ZonedDateTime bEnd) {
        long startMax = Math.max(aStart.toInstant().getEpochSecond(), bStart.toInstant().getEpochSecond());
        long endMin = Math.min(aEnd.toInstant().getEpochSecond(), bEnd.toInstant().getEpochSecond());
        return startMax <= endMin;
    }
}
