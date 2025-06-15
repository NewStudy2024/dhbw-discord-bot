package dhbw.mos.bot.discord

import dhbw.mos.bot.config.BackendName
import dhbw.mos.bot.config.ConfigManager
import kotlinx.serialization.Serializable

@Serializable
@BackendName("discord")
data class DiscordConfig(
    val token: String,
    val discussionsChannel: Long,
    val calendarChannel: Long,
    val calendarNotificationMessage: String,
    val deadlinesChannel: Long
) {
    companion object {
        @JvmStatic
        fun createManager() = ConfigManager.create<DiscordConfig>()
    }
}