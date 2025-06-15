package dhbw.mos.bot.discord;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import dhbw.mos.bot.Backend;
import dhbw.mos.bot.Common;
import dhbw.mos.bot.cal.Event;
import dhbw.mos.bot.config.Config;
import dhbw.mos.bot.config.ConfigManager;
import dhbw.mos.bot.discord.commands.ListTrackedRepositories;
import dhbw.mos.bot.discord.commands.TrackRepository;
import dhbw.mos.bot.discord.commands.UntrackRepository;
import dhbw.mos.bot.discord.interactions.DeadlineCreateButton;
import dhbw.mos.bot.discord.interactions.DeadlineCreateModal;
import dhbw.mos.bot.discord.interactions.DeadlineDeleteMenu;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class DiscordBackend implements Backend {
    private static final Logger log = LoggerFactory.getLogger(DiscordBackend.class);

    private final ConfigManager<DiscordConfig> configManager = DiscordConfig.createManager();
    private final Common common = new Common(this);
    private final JDA jda = JDABuilder.createLight(configManager.getConfig().getBackend().getToken()).build();

    public static void main(String[] args) {
        new DiscordBackend().initialize();
    }

    public void initialize() {
        try {
            this.jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        common.initialize();

        this.jda.addEventListener(
                new CommandClientBuilder().useHelpBuilder(false).setActivity(null).setOwnerId(0).addSlashCommands(
                        new TrackRepository(this),
                        new UntrackRepository(this),
                        new ListTrackedRepositories(this)
                ).build(),
                new DeadlineCreateButton(),
                new DeadlineCreateModal(this),
                new DeadlineDeleteMenu(this)
        );
    }

    public JDA getJda() {
        return jda;
    }

    public Common getCommon() {
        return common;
    }

    @Override
    public ConfigManager<DiscordConfig> getConfigManager() {
        return configManager;
    }

    @Override
    public void postDiscussionNotification(String author, String title, String url, Runnable posted) {
        long discussionChannelId = getConfigManager().getConfig().getBackend().getDiscussionsChannel();
        TextChannel discussionChannel = getJda().getTextChannelById(discussionChannelId);
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

        withFirstPinnedMessage(
                calendarChannel,
                message -> message.editMessageEmbeds(messageBuilder.build()).queue(),
                channel -> channel.sendMessageEmbeds(messageBuilder.build())
                        .queue(message -> message.pin().queue())
        );

    }

    @Override
    public void sendCalendarEventNotification() {
        TextChannel calendarChannel = getCalendarChannel();
        if (calendarChannel == null) return;

        calendarChannel.sendMessage(getConfigManager().getConfig().getBackend().getCalendarNotificationMessage())
                .queue(message -> message.delete().queue());

        withFirstPinnedMessage(
                calendarChannel,
                message -> message.clearReactions().queue(v -> {
                    message.addReaction(Emoji.fromUnicode("⌛")).queue();
                    message.addReaction(Emoji.fromUnicode("\uD83C\uDDF1")).queue(); // L
                    message.addReaction(Emoji.fromUnicode("\uD83C\uDDE6")).queue(); // A
                    message.addReaction(Emoji.fromUnicode("\uD83C\uDDF9")).queue(); // T
                    message.addReaction(Emoji.fromUnicode("\uD83C\uDDEA")).queue(); // E
                }),
                channel -> {
                }
        );
    }

    @Override
    public void updateDeadlines(List<Config.Deadline> deadlines) {
        long deadlinesChannelId = getConfigManager().getConfig().getBackend().getDeadlinesChannel();
        TextChannel deadlinesChannel = getJda().getTextChannelById(deadlinesChannelId);
        if (deadlinesChannel == null) {
            log.error("Invalid deadlines channel id");
            return;
        }

        MessageCreateBuilder messageBuilder = new MessageCreateBuilder().setContent("# Deadlines");

        for (int i = 0; i < deadlines.size(); i++) {
            Config.Deadline deadline = deadlines.get(i);

            Color color = Color.getHSBColor(1.0f / deadlines.size() * i + 0.1f, 1.0f, 1.0f);

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle(deadline.getName())
                    .setDescription(deadline.getDescription())
                    .setFooter(deadline.getDate())
                    .setColor(color)
                    .build();
            messageBuilder.addEmbeds(embed);
        }
        messageBuilder.addActionRow(Button.success(DeadlineCreateButton.COMPONENT_ID, "Add deadline"));

        if (!deadlines.isEmpty()) {
            List<SelectOption> options = deadlines.stream()
                    .map(deadline -> SelectOption.of(deadline.getName(), String.valueOf(deadline.getId())))
                    .toList();

            StringSelectMenu.Builder deleteMenuBuilder = StringSelectMenu.create(DeadlineDeleteMenu.COMPONENT_ID)
                    .setPlaceholder("Delete deadline")
                    .addOptions(options);

            messageBuilder.addActionRow(deleteMenuBuilder.build());
        }

        withFirstPinnedMessage(
                deadlinesChannel,
                message -> message.editMessage(MessageEditData.fromCreateData(messageBuilder.build())).queue(),
                channel -> channel.sendMessage(messageBuilder.build())
                        .queue(message -> message.pin().queue())
        );
    }

    private @Nullable TextChannel getCalendarChannel() {
        long calendarChannelId = getConfigManager().getConfig().getBackend().getCalendarChannel();
        TextChannel calendarChannel = getJda().getTextChannelById(calendarChannelId);
        if (calendarChannel == null) {
            log.error("Invalid calendar channel id");
        }
        return calendarChannel;
    }

    private void withFirstPinnedMessage(TextChannel channel, Consumer<Message> withMessage, Consumer<TextChannel> notFound) {
        channel.retrievePinnedMessages().queue(pinned -> pinned.stream()
                .filter(message -> message.getAuthor() == message.getJDA().getSelfUser())
                .findFirst()
                .ifPresentOrElse(withMessage, () -> notFound.accept(channel)));
    }
}
