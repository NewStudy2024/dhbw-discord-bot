package dhbw.mos.bot.discord.interactions;

import dhbw.mos.bot.discord.DiscordBackend;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class DeadlineCreateModal extends ListenerAdapter {
    public static final String MODAL_ID = "deadline-create-modal";
    public static final String NAME_ID = "name";
    public static final String DESCRIPTION_ID = "description";
    public static final String DATE_ID = "date";

    private final DiscordBackend backend;

    public DeadlineCreateModal(DiscordBackend backend) {
        this.backend = backend;
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (!event.getModalId().equals(MODAL_ID)) return;

        String name = Objects.requireNonNull(event.getValue(NAME_ID)).getAsString();
        @Nullable String description = Optional.ofNullable(event.getValue(DESCRIPTION_ID))
                .map(ModalMapping::getAsString)
                .orElse(null);
        String date = Objects.requireNonNull(event.getValue(DATE_ID)).getAsString();

        backend.getCommon().getDeadlinesService().addDeadline(name, description, date);

        event.deferEdit().queue();
    }
}
