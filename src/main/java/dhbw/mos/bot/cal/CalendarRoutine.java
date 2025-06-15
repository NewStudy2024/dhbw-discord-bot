package dhbw.mos.bot.cal;

import dhbw.mos.bot.Common;
import dhbw.mos.bot.Util;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

public class CalendarRoutine {
    private static final Duration NOTIFICATION_OFFSET = Duration.ofMinutes(5);

    private final Common common;
    private Instant lastNotified = Instant.EPOCH;


    public CalendarRoutine(Common common) {
        this.common = common;
    }

    public void initialize() {
        Util.scheduleAtRate("Calendar update routine", this::updateCalendar, Duration.ofSeconds(60), Duration.ofSeconds(1));
        Util.scheduleAtRate("Calendar notification routine", this::sendEventNotifications, Duration.ofSeconds(5), Duration.ofSeconds(5));
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
