package dhbw.mos.bot;

import dhbw.mos.bot.bridge.BotBridge;
import dhbw.mos.bot.config.Config;

import java.util.List;

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

    @Override
    public void untrackRepository(String owner, String name) {
        bot.getConfigManager().getConfig().getTrackedRepos()
                .removeIf(repo -> repo.getOwner().equalsIgnoreCase(owner) && repo.getName().equalsIgnoreCase(name));
        bot.getConfigManager().save();
    }

    @Override
    public List<String> listTrackedRepositories() {
        return bot.getConfigManager().getConfig().getTrackedRepos().stream()
                .map(repo -> "%s/%s".formatted(repo.getOwner(), repo.getName()))
                .toList();
    }
}
