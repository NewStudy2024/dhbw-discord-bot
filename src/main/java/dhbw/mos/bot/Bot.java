package dhbw.mos.bot;

import dhbw.mos.bot.bridge.BackendBridge;
import dhbw.mos.bot.bridge.BotBridge;
import dhbw.mos.bot.cal.CalendarRoutine;
import dhbw.mos.bot.cal.CalendarService;
import dhbw.mos.bot.config.ConfigManager;
import dhbw.mos.bot.github.DiscussionsRoutine;

import java.util.Optional;

public class Bot {
    private final ConfigManager<?> configManager;
    private final DiscussionsRoutine discussionsRoutine;
    private final CalendarService calendarService;
    private final CalendarRoutine calendarRoutine;
    private final BotBridge bridge;
    private Optional<BackendBridge> backend = Optional.empty();

    public Bot(ConfigManager<?> configManager) {
        this.configManager = configManager;
        this.discussionsRoutine = new DiscussionsRoutine(this);
        this.calendarService = new CalendarService(configManager);
        this.calendarRoutine = new CalendarRoutine(this);
        this.bridge = new BotBridgeImpl(this);
    }

    public ConfigManager<?> getConfigManager() {
        return configManager;
    }

    public CalendarService getCalendarService() {
        return calendarService;
    }

    public BotBridge getBridge() {
        return bridge;
    }

    public Optional<BackendBridge> getBackend() {
        return backend;
    }

    public void setBackendBridge(Optional<BackendBridge> backendBridge) {
        this.backend = backendBridge;
    }
}
