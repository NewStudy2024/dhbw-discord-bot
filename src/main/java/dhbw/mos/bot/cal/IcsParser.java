package dhbw.mos.bot.cal;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class IcsParser {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    private static final List<String> RELEVANT_FIELDS = List.of("DTEND", "DTSTART", "SUMMARY");

    static {
        if (!RELEVANT_FIELDS.equals(RELEVANT_FIELDS.stream().sorted().toList())) {
            throw new IllegalStateException("ICS RELEVANT_FIELDS are ordered incorrectly");
        }
    }

    public static List<Event> parse(String icsContent) {
        return icsContent
                .split("BEGIN:VEVENT", 2)[1]
                .lines()
                .filter(line -> !line.isBlank())
                .map(line -> line.split("[:;]", 2))
                .filter(line -> RELEVANT_FIELDS.contains(line[0]))
                .collect(
                        () -> new ArrayList<List<String[]>>(),
                        (acc, line) -> {
                            if (acc.isEmpty() || acc.getLast().size() == RELEVANT_FIELDS.size()) {
                                acc.add(new ArrayList<>());
                            }
                            acc.getLast().add(line);
                        },
                        ArrayList::addAll
                )
                .stream()
                .map(event -> event.stream()
                        .sorted(Comparator.comparing(a -> a[0]))
                        .map(line -> line[1])
                        .toList()
                )
                .map(event -> {
                    return new Event(
                            parseTimeField(event.get(1)),
                            parseTimeField(event.get(0)),
                            event.get(2).strip()
                    );
                })
                .toList();
    }

    private static ZonedDateTime parseTimeField(String timeFieldValue) {
        String[] parts = timeFieldValue.split(":");
        String timeZone = parts[0].split("=")[1];
        String timeStamp = parts[1];
        return ZonedDateTime.parse(timeStamp, FORMATTER.withZone(ZoneId.of(timeZone)));
    }
}
