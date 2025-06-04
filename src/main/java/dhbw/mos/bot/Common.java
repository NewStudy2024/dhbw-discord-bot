package dhbw.mos.bot;

import dhbw.mos.bot.cal.CalendarRoutine;
import dhbw.mos.bot.cal.CalendarService;
import dhbw.mos.bot.config.Config;
import dhbw.mos.bot.config.ConfigManager;
import dhbw.mos.bot.github.DiscussionsRoutine;

import java.util.List;

public class Common {
    private final Backend backend;
    private final DiscussionsRoutine discussionsRoutine;
    private final CalendarService calendarService;
    private final CalendarRoutine calendarRoutine;

    public Common(Backend backend) {
        this.backend = backend;
        discussionsRoutine = new DiscussionsRoutine(this);
        calendarService = new CalendarService(this);
        calendarRoutine = new CalendarRoutine(this);
    }

    public void initialize() {
        discussionsRoutine.initialize();
        calendarService.initialize();
        calendarRoutine.initialize();
    }

    public Backend getBackend() {
        return backend;
    }

    public ConfigManager<?> getConfigManager() {
        return backend.getConfigManager();
    }

    public CalendarService getCalendarService() {
        return calendarService;
    }

    public void trackRepository(String owner, String name) {
        getConfigManager().getConfig().getTrackedRepos().add(new Config.TrackedRepo(owner, name));
        getConfigManager().save();
    }

    public void untrackRepository(String owner, String name) {
        getConfigManager().getConfig().getTrackedRepos()
                .removeIf(repo -> repo.getOwner().equalsIgnoreCase(owner) && repo.getName().equalsIgnoreCase(name));
        getConfigManager().save();
    }

    public List<String> listTrackedRepositories() {
        return getConfigManager().getConfig().getTrackedRepos().stream()
                .map(repo -> "%s/%s".formatted(repo.getOwner(), repo.getName()))
                .toList();
    }
}
