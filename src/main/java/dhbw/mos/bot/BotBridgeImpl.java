package dhbw.mos.bot;

import dhbw.mos.bot.bridge.BotBridge;
import dhbw.mos.bot.config.Config;

public class BotBridgeImpl implements BotBridge {
    private final Bot bot;

    public BotBridgeImpl(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void trackRepository(String owner, String name) {
        bot.getConfigManager().getConfig().getTrackedRepos().add(new Config.TrackedRepo(owner, name));
        bot.getConfigManager().save();
    }
}
