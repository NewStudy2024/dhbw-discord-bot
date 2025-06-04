package dhbw.mos.bot.cal;

import dhbw.mos.bot.Common;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CalendarRoutine {
    private static final Duration NOTIFICATION_OFFSET = Duration.ofMinutes(5);

    private final Common common;
    private Instant lastNotified = Instant.EPOCH;


    public CalendarRoutine(Common common) {
        this.common = common;
    }

    public void initialize() {
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
        common.getCalendarService().reload();

        ZonedDateTime todayStart = common.getCalendarService().getNow().withHour(0).withMinute(0).withSecond(0);
        ZonedDateTime todayEnd = todayStart.plusDays(1);


        List<Event> eventsToday = common.getCalendarService().getEvents()
                .stream()
                .filter(event -> doesTimeOverlap(todayStart, todayEnd, event.start(), event.end()))
                .toList();

        common.getBackend().updateCalendarData(eventsToday);
    }

    private void sendEventNotifications() {
        ZonedDateTime now = common.getCalendarService().getNow();

        if (lastNotified.plus(NOTIFICATION_OFFSET).isAfter(now.toInstant())) return;

        common.getCalendarService().getEvents().stream()
                .filter(event -> event.start().isAfter(now))
                .filter(event -> event.start().isBefore(now.plus(NOTIFICATION_OFFSET)))
                .findFirst()
                .ifPresent(_event -> {
                    common.getBackend().sendCalendarEventNotification();
                    lastNotified = Instant.now();
                });
    }

    private boolean doesTimeOverlap(ZonedDateTime aStart, ZonedDateTime aEnd, ZonedDateTime bStart, ZonedDateTime bEnd) {
        long startMax = Math.max(aStart.toInstant().getEpochSecond(), bStart.toInstant().getEpochSecond());
        long endMin = Math.min(aEnd.toInstant().getEpochSecond(), bEnd.toInstant().getEpochSecond());
        return startMax <= endMin;
    }
}
