package dhbw.mos.bot.discord.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import dhbw.mos.bot.discord.DiscordBackend;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Objects;

public class UntrackRepository extends SlashCommand {
    private static final String OWNED_REPOSITORY = "repository";

    private final DiscordBackend backend;

    public UntrackRepository(DiscordBackend backend) {
        this.backend = backend;
        this.name = "untrack-repository";
        this.options = List.of(
                new OptionData(OptionType.STRING, OWNED_REPOSITORY, "Repository to untrack", true, true)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String[] ownedRepo = Objects.requireNonNull(event.getOption(OWNED_REPOSITORY)).getAsString().split("/");
        String owner = ownedRepo[0];
        String repo = ownedRepo.length > 1 ? ownedRepo[1] : "";

        backend.getCommon().untrackRepository(owner, repo);
        event.reply("Not tracking `%s/%s` anymore".formatted(owner, repo)).setEphemeral(true).queue();
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        event.replyChoiceStrings(
                backend.getCommon()
                        .listTrackedRepositories()
                        .stream()
                        .map(repo ->
                                "%s/%s".formatted(repo.getOwner(), repo.getName())
                        ).toArray(String[]::new)
        ).queue();
    }
}
