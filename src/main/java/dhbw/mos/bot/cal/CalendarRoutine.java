package dhbw.mos.bot.cal;

import dhbw.mos.bot.Bot;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CalendarRoutine {
    private final Bot bot;


    public CalendarRoutine(Bot bot) {
        this.bot = bot;

        TimerTask updateTask = new TimerTask() {
            @Override
            public void run() {
                CalendarRoutine.this.updateCalendar();
            }
        };

        new Timer("Calendar update routine")
                .scheduleAtFixedRate(updateTask, 1000, Duration.ofSeconds(60).toMillis());
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

    private boolean doesTimeOverlap(ZonedDateTime aStart, ZonedDateTime aEnd, ZonedDateTime bStart, ZonedDateTime bEnd) {
        long startMax = Math.max(aStart.toInstant().getEpochSecond(), bStart.toInstant().getEpochSecond());
        long endMin = Math.min(aEnd.toInstant().getEpochSecond(), bEnd.toInstant().getEpochSecond());
        return startMax <= endMin;
    }
}
