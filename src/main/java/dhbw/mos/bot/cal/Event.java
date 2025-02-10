package dhbw.mos.bot.cal;

import java.time.ZonedDateTime;

public record Event(ZonedDateTime start, ZonedDateTime end, String summary) {
}
