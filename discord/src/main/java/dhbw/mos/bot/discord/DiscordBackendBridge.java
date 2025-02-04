package dhbw.mos.bot.discord;

import dhbw.mos.bot.bridge.BackendBridge;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordBackendBridge implements BackendBridge {
    private static final Logger log = LoggerFactory.getLogger(DiscordBackendBridge.class);

    private final DiscordBackend backend;

    public DiscordBackendBridge(DiscordBackend backend) {
        this.backend = backend;
    }

    @Override
    public void postDiscussionNotification(String author, String title, String url, Runnable posted) {
        long discussionChannelId = backend.getConfigManager().getConfig().getBackend().getDiscussionsChannel();
        TextChannel discussionChannel = backend.getJda().getTextChannelById(discussionChannelId);
        if (discussionChannel == null) {
            log.error("Invalid discussion channel id");
            return;
        }

        MessageEmbed message = new EmbedBuilder()
                .setTitle(title)
                .setAuthor(author)
                .setUrl(url)
                .build();

        discussionChannel.sendMessageEmbeds(message).queue(m -> posted.run());
    }
}
