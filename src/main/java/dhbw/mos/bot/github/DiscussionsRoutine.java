package dhbw.mos.bot.github;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloClient;
import dhbw.mos.bot.Common;
import dhbw.mos.bot.config.Config;
import dhbw.mos.bot.github.graphql.DiscussionsQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DiscussionsRoutine {
    private static final Logger log = LoggerFactory.getLogger(DiscussionsRoutine.class);

    private final Common common;
    private final ApolloClient client;
    private int currentRepoIndex = 0;

    public DiscussionsRoutine(Common common) {
        String token = common.getConfigManager().getConfig().getGithubToken();

        this.common = common;
        this.client = new ApolloClient.Builder()
                .serverUrl("https://api.github.com/graphql")
                .addHttpHeader("Authorization", "Bearer " + token)
                .build();
    }

    public void initialize() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                DiscussionsRoutine.this.queryDiscussions();
            }
        };

        new Timer("Discussions Routine").scheduleAtFixedRate(task, 1000, Duration.ofSeconds(5).toMillis());
    }

    private void queryDiscussions() {
        List<Config.TrackedRepo> trackedRepos = common.getConfigManager().getConfig().getTrackedRepos();
        int trackedCount = trackedRepos.size();

        if (trackedCount == 0) return;
        currentRepoIndex %= trackedCount;

        if (currentRepoIndex < trackedCount) {
            Config.TrackedRepo repo = trackedRepos.get(currentRepoIndex);
            currentRepoIndex++;

            DiscussionsQuery query = DiscussionsQuery.builder()
                    .owner(repo.getOwner())
                    .name(repo.getName())
                    .build();

            client.query(query).enqueue(data -> handleDiscussionsResponse(repo, data));
        }
    }

    private void handleDiscussionsResponse(
            Config.TrackedRepo trackedRepo,
            ApolloResponse<DiscussionsQuery.Data> response
    ) {
        DiscussionsQuery.Data data = response.dataOrThrow();
        log.debug(data.toString());

        if (data.repository == null) {
            log.warn("Unable to find repository: {}", trackedRepo.toString());
            return;
        }

        for (DiscussionsQuery.Node discussion : data.repository.discussions.nodes.reversed()) {
            if (trackedRepo.getLatestKnownId() >= discussion.number) continue;
            common.getBackend().postDiscussionNotification(
                    trackedRepo.getOwner(),
                    discussion.title,
                    discussion.url.toString(),
                    () -> {
                        trackedRepo.setLatestKnownId(discussion.number);
                        common.getConfigManager().save();
                    }
            );
        }
    }
}
