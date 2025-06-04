package dhbw.mos.bot.discord.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import dhbw.mos.bot.discord.DiscordBackend;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Objects;

public class TrackRepository extends SlashCommand {
    private static final String OPTION_OWNER = "owner";
    private static final String OPTION_REPO = "repo";
    private static final String OPTION_REMOVE = "remove";

    private final DiscordBackend backend;

    public TrackRepository(DiscordBackend backend) {
        this.backend = backend;
        this.name = "track-repository";
        this.options = List.of(
                new OptionData(OptionType.STRING, OPTION_OWNER, "Owner of the repository", true),
                new OptionData(OptionType.STRING, OPTION_REPO, "The repository to track", true),
                new OptionData(OptionType.BOOLEAN, OPTION_REMOVE, "Untrack the repository", false)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String owner = Objects.requireNonNull(event.getOption(OPTION_OWNER)).getAsString();
        String repo = Objects.requireNonNull(event.getOption(OPTION_REPO)).getAsString();
        boolean remove = event.optBoolean(OPTION_REMOVE);

        if (!remove) {
            backend.getCommon().trackRepository(owner, repo);
            event.reply("Now tracking `%s/%s`".formatted(owner, repo)).setEphemeral(true).queue();
        } else {
            backend.getCommon().untrackRepository(owner, repo);
            event.reply("Not tracking `%s/%s` anymore".formatted(owner, repo)).setEphemeral(true).queue();
        }
    }
}
