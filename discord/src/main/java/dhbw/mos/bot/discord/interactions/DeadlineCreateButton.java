package dhbw.mos.bot.discord.interactions;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class DeadlineCreateButton extends ListenerAdapter {
    public static final String COMPONENT_ID = "deadline-create";

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().equals(COMPONENT_ID)) return;

        Modal modal = Modal.create(DeadlineCreateModal.MODAL_ID, "Add deadline")
                .addActionRow(TextInput.create(DeadlineCreateModal.NAME_ID, "Name", TextInputStyle.SHORT).setRequired(true).build())
                .addActionRow(TextInput.create(DeadlineCreateModal.DESCRIPTION_ID, "Description", TextInputStyle.PARAGRAPH).setRequired(false).build())
                .addActionRow(TextInput.create(DeadlineCreateModal.DATE_ID, "Date", TextInputStyle.SHORT).setRequired(true).build())
                .build();

        event.replyModal(modal).queue();
    }
}
