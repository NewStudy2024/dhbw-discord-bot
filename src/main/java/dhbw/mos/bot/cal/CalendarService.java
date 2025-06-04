package dhbw.mos.bot.cal;

import dhbw.mos.bot.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class CalendarService {
    private static final Logger log = LoggerFactory.getLogger(CalendarService.class);
    public static final ZoneId TIMEZONE = ZoneId.of("Europe/Berlin");

    private final Common common;
    private List<Event> events = List.of();


    public CalendarService(Common common) {
        this.common = common;
    }

    public void initialize() {
        events = loadEvents();
    }

    public List<Event> getEvents() {
        return events;
    }

    public void reload() {
        events = loadEvents();
    }

    public ZonedDateTime getNow() {
        return ZonedDateTime.now(TIMEZONE);
    }

    private List<Event> loadEvents() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(common.getConfigManager().getConfig().getCalendarUrl()))
                .build();

        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return IcsParser.parse(response.body());
        } catch (Exception e) {
            log.error("Unable to load calendar events", e);
        }

        return List.of();
    }
}
