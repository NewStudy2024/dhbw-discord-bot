package dhbw.mos.bot.github;

import dhbw.mos.bot.Common;
import dhbw.mos.bot.config.Config;

import java.util.List;

public class DiscussionService {
    private final Common common;

    public DiscussionService(Common common) {
        this.common = common;
    }

    public void trackRepository(String owner, String name) {
        common.getConfigManager().getConfig().getTrackedRepos().add(new Config.TrackedRepo(owner, name));
        common.getConfigManager().save();
    }

    public void untrackRepository(String owner, String name) {
        common.getConfigManager().getConfig().getTrackedRepos()
                .removeIf(repo -> repo.getOwner().equalsIgnoreCase(owner) && repo.getName().equalsIgnoreCase(name));
        common.getConfigManager().save();
    }

    public List<Config.TrackedRepo> listTrackedRepositories() {
        return common.getConfigManager().getConfig().getTrackedRepos().stream().toList();
    }
}
