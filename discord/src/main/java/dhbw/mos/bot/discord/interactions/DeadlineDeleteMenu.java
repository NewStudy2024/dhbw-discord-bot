package dhbw.mos.bot.discord.interactions;

import dhbw.mos.bot.discord.DiscordBackend;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DeadlineDeleteMenu extends ListenerAdapter {
    public static final String COMPONENT_ID = "deadline-delete-menu";

    private final DiscordBackend backend;

    public DeadlineDeleteMenu(DiscordBackend backend) {
        this.backend = backend;
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals(COMPONENT_ID)) return;

        long id = Long.parseLong(event.getValues().getFirst());
        backend.getCommon().getDeadlinesService().deleteDeadlineById(id);

        event.deferEdit().queue();
    }
}
