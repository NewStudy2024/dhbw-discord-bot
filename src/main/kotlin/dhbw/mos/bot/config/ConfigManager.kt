package dhbw.mos.bot.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

class ConfigManager<BackendConfig> private constructor(
    private val serializer: KSerializer<Config<BackendConfig>>, val path: Path, val config: Config<BackendConfig>
) {
    companion object {
        val json = Json { prettyPrint = true }

        @JvmStatic
        inline fun <reified BackendConfig> create(path: Path = Path("./config.json")): ConfigManager<BackendConfig> {
            return ConfigManager(Json.serializersModule.serializer<Config<BackendConfig>>(), path)
        }
    }

    constructor(serializer: KSerializer<Config<BackendConfig>>, path: Path) : this(
        serializer, path, json.decodeFromString(serializer, path.readText())
    )

    init {
        save()
    }

    fun save() {
        path.writeText(json.encodeToString(serializer, config))
    }
}
