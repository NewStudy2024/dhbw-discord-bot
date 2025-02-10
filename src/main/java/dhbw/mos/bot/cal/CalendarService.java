package dhbw.mos.bot.cal;

import dhbw.mos.bot.config.ConfigManager;
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

    private final ConfigManager<?> configManager;
    private List<Event> events;


    public CalendarService(ConfigManager<?> configManager) {
        this.configManager = configManager;
        this.events = loadEvents();
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
                .uri(URI.create(configManager.getConfig().getCalendarUrl()))
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
