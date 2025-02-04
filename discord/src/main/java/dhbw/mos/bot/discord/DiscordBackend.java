package dhbw.mos.bot.discord;

import dhbw.mos.bot.Bot;
import dhbw.mos.bot.bridge.BackendBridge;
import dhbw.mos.bot.bridge.BotBridge;
import dhbw.mos.bot.config.ConfigManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.util.Optional;

public class DiscordBackend {
    private final ConfigManager<DiscordConfig> configManager;
    private final BackendBridge bridge;
    private Optional<BotBridge> bot = Optional.empty();
    private final JDA jda;

    public DiscordBackend(ConfigManager<DiscordConfig> configManager) {
        this.configManager = configManager;
        this.bridge = new DiscordBackendBridge(this);
        this.jda = JDABuilder.createLight(configManager.getConfig().getBackend().getToken()).build();

        this.jda.addEventListener(new SlashCommandHandler(this));
    }

    public static void main(String[] args) {
        ConfigManager<DiscordConfig> configManager = DiscordConfig.createManager();
        DiscordBackend backend = new DiscordBackend(configManager);
        Bot bot = new Bot(configManager);
        bot.setBackendBridge(Optional.of(backend.getBridge()));
        backend.setBotBridge(Optional.of(bot.getBridge()));
    }

    public ConfigManager<DiscordConfig> getConfigManager() {
        return configManager;
    }

    public BackendBridge getBridge() {
        return bridge;
    }

    public Optional<BotBridge> getBot() {
        return bot;
    }

    public void setBotBridge(Optional<BotBridge> botBridge) {
        this.bot = botBridge;
    }

    public JDA getJda() {
        return jda;
    }
}
