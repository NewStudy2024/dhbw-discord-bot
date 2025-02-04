package dhbw.mos.bot.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class SlashCommandHandler extends ListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(SlashCommandHandler.class);
    private static final String TRACK_REPOSITORY = "track-repository";
    private static final String TRACK_REPOSITORY_OWNER = "owner";
    private static final String TRACK_REPOSITORY_REPO = "repo";

    private final DiscordBackend backend;

    public SlashCommandHandler(DiscordBackend backend) {
        this.backend = backend;
        backend.getJda().updateCommands().addCommands(
                Commands.slash(TRACK_REPOSITORY, "Add a repository to track blog posts for")
                        .addOption(OptionType.STRING, TRACK_REPOSITORY_OWNER, "Owner of the repository", true)
                        .addOption(OptionType.STRING, TRACK_REPOSITORY_REPO, "The repository to track", true)
        ).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case TRACK_REPOSITORY -> {
                String owner = Objects.requireNonNull(event.getOption(TRACK_REPOSITORY_OWNER)).getAsString();
                String repo = Objects.requireNonNull(event.getOption(TRACK_REPOSITORY_REPO)).getAsString();
                backend.getBot().ifPresent(bot -> {
                    bot.trackRepository(owner, repo);
                    event.reply("Now tracking `%s/%s`".formatted(owner, repo)).setEphemeral(true).queue();
                });
            }
            default -> log.warn("Unknown slash command {}", event);
        }
    }
}
