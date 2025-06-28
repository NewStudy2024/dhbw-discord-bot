package dhbw.mos.bot;

import dhbw.mos.bot.cal.CalendarRoutine;
import dhbw.mos.bot.cal.CalendarService;
import dhbw.mos.bot.config.ConfigManager;
import dhbw.mos.bot.deadlines.DeadlinesRoutine;
import dhbw.mos.bot.deadlines.DeadlinesService;
import dhbw.mos.bot.github.DiscussionService;
import dhbw.mos.bot.github.DiscussionsRoutine;

public class Common {
    private final Backend backend;
    private final DiscussionService discussionService;
    private final DiscussionsRoutine discussionsRoutine;
    private final CalendarService calendarService;
    private final CalendarRoutine calendarRoutine;
    private final DeadlinesService deadlinesService;
    private final DeadlinesRoutine deadlinesRoutine;

    public Common(Backend backend) {
        this.backend = backend;
        discussionService = new DiscussionService(this);
        discussionsRoutine = new DiscussionsRoutine(this);
        calendarService = new CalendarService(this);
        calendarRoutine = new CalendarRoutine(this);
        deadlinesService = new DeadlinesService(this);
        deadlinesRoutine = new DeadlinesRoutine(this);
    }

    public void initialize() {
        discussionsRoutine.initialize();
        calendarService.initialize();
        calendarRoutine.initialize();
        deadlinesRoutine.initialize();
    }

    public Backend getBackend() {
        return backend;
    }

    public ConfigManager<?> getConfigManager() {
        return backend.getConfigManager();
    }

    public DiscussionService getDiscussionService() {
        return discussionService;
    }

    public CalendarService getCalendarService() {
        return calendarService;
    }

    public DeadlinesService getDeadlinesService() {
        return deadlinesService;
    }
}
