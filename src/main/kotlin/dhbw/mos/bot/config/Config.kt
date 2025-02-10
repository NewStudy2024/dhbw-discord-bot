package dhbw.mos.bot.config

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@Serializable
data class Config<BackendConfig>(
    @SerialName("backend")
    private val backendWrapped: BackendConfigWrapper<BackendConfig>,
    val githubToken: String,
    @EncodeDefault
    val trackedRepos: MutableList<TrackedRepo> = mutableListOf(),
    val calendarUrl: String,
) {
    val backend get() = backendWrapped.backendConfig

    @Serializable
    data class TrackedRepo @JvmOverloads constructor(
        val owner: String,
        val name: String,
        @EncodeDefault
        var latestKnownId: Int = -1
    )
}

@KeepGeneratedSerializer
@Serializable(with = BackendSerializer::class)
data class BackendConfigWrapper<BackendConfig>(val backendConfig: BackendConfig, val otherConfigs: JsonObject)


class BackendSerializer<B>(private val backendSerializer: KSerializer<B>) :
    KSerializer<BackendConfigWrapper<B>> {
    private val generated = BackendConfigWrapper.generatedSerializer(backendSerializer).descriptor;
    private val backendName = backendSerializer.descriptor.annotations
        .filterIsInstance<BackendName>()
        .firstOrNull()
        ?.name ?: error("${backendSerializer.descriptor.serialName} is missing the BackendName annotation")

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(generated.serialName) {
        this.element(backendName, backendSerializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): BackendConfigWrapper<B> {
        val json = decoder.decodeSerializableValue(JsonObject.serializer())
        val backendObject = json[backendName] ?: error("No config for backend '$backendName'")
        val backendConfig = Json.decodeFromJsonElement(backendSerializer, backendObject)
        val otherConfigs = JsonObject(json - backendName)
        return BackendConfigWrapper(backendConfig, otherConfigs)
    }


    override fun serialize(encoder: Encoder, value: BackendConfigWrapper<B>) {
        val backendObject = Json.encodeToJsonElement(backendSerializer, value.backendConfig)
        val json = JsonObject(value.otherConfigs + Pair(backendName, backendObject))
        encoder.encodeSerializableValue(JsonObject.serializer(), json)
    }

}