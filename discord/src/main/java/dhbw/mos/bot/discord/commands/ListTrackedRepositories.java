package dhbw.mos.bot.discord.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import dhbw.mos.bot.discord.DiscordBackend;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ListTrackedRepositories extends SlashCommand {
    private final DiscordBackend backend;

    public ListTrackedRepositories(DiscordBackend backend) {
        this.backend = backend;
        this.name = "list-tracked-repositories";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        MessageEmbed message = new EmbedBuilder()
                .setDescription(String.join(
                        "\n",
                        backend.getCommon()
                                .listTrackedRepositories()
                                .stream()
                                .map(repo -> "- %s/%s".formatted(repo.getOwner(), repo.getName()))
                                .toList()
                ))
                .build();
        event.replyEmbeds(message).setEphemeral(true).queue();
    }
}
