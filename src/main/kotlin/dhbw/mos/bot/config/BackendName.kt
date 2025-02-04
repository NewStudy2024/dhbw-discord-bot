package dhbw.mos.bot.config

import kotlinx.serialization.SerialInfo

@SerialInfo
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class BackendName(val name: String)
