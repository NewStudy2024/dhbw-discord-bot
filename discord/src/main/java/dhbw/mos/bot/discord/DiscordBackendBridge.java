package dhbw.mos.bot.discord;

import dhbw.mos.bot.bridge.BackendBridge;
import dhbw.mos.bot.cal.Event;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

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

    @Override
    public void updateCalendarData(List<Event> events) {
        TextChannel calendarChannel = getCalendarChannel();
        if (calendarChannel == null) return;

        EmbedBuilder messageBuilder = events.stream().collect(
                EmbedBuilder::new,
                (builder, event) -> {
                    String timeStamp = "<t:" + event.start().toInstant().getEpochSecond() + ":F>";
                    builder.addField(event.summary(), timeStamp, false);
                },
                EmbedBuilder::copyFrom
        );
        messageBuilder.setTitle("Calendar");

        withFirstPinnedMessageMessage(
                calendarChannel,
                message -> message.editMessageEmbeds(messageBuilder.build()).queue(),
                channel -> {
                    channel.sendMessageEmbeds(messageBuilder.build())
                            .queue(message -> message.pin().queue());
                }
        );

    }

    @Override
    public void sendCalendarEventNotification() {
        TextChannel calendarChannel = getCalendarChannel();
        if (calendarChannel == null) return;

        calendarChannel.sendMessage(backend.getConfigManager().getConfig().getBackend().getCalendarNotificationMessage())
                .queue(message -> message.delete().queue());

        withFirstPinnedMessageMessage(
                calendarChannel,
                message -> {
                    message.clearReactions().queue(v -> {
                        message.addReaction(Emoji.fromUnicode("⌛")).queue();
                        message.addReaction(Emoji.fromUnicode("\uD83C\uDDF1")).queue(); // L
                        message.addReaction(Emoji.fromUnicode("\uD83C\uDDE6")).queue(); // A
                        message.addReaction(Emoji.fromUnicode("\uD83C\uDDF9")).queue(); // T
                        message.addReaction(Emoji.fromUnicode("\uD83C\uDDEA")).queue(); // E
                    });
                },
                channel -> {}
        );
    }

    private @Nullable TextChannel getCalendarChannel() {
        long calendarChannelId = backend.getConfigManager().getConfig().getBackend().getCalendarChannel();
        TextChannel calendarChannel = backend.getJda().getTextChannelById(calendarChannelId);
        if (calendarChannel == null) {
            log.error("Invalid calendar channel id");
        }
        return calendarChannel;
    }

    private void withFirstPinnedMessageMessage(TextChannel channel, Consumer<Message> withMessage, Consumer<TextChannel> notFound) {
        channel.retrievePinnedMessages().queue(pinned -> {
            pinned.stream()
                    .filter(message -> message.getAuthor() == message.getJDA().getSelfUser())
                    .findFirst()
                    .ifPresentOrElse(withMessage, () -> notFound.accept(channel));
        });
    }
}
