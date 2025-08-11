package dev.losterixx.sCore.utils

import com.nexomc.nexo.utils.serialize
import dev.losterixx.sCore.Main
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

object MMUtil {

    private val mm = Main.miniMessage

    fun translateLegacyCodes(message: String): String {
        return message
            .replace(Regex("&([0-9a-fA-Fk-orK-OR])")) { matchResult ->
                when (val code = matchResult.groupValues[1].lowercase()) {
                    "0" -> "<black>"
                    "1" -> "<dark_blue>"
                    "2" -> "<dark_green>"
                    "3" -> "<dark_aqua>"
                    "4" -> "<dark_red>"
                    "5" -> "<dark_purple>"
                    "6" -> "<gold>"
                    "7" -> "<gray>"
                    "8" -> "<dark_gray>"
                    "9" -> "<blue>"
                    "a" -> "<green>"
                    "b" -> "<aqua>"
                    "c" -> "<red>"
                    "d" -> "<light_purple>"
                    "e" -> "<yellow>"
                    "f" -> "<white>"
                    "k" -> "<obfuscated>"
                    "l" -> "<bold>"
                    "m" -> "<strikethrough>"
                    "n" -> "<underlined>"
                    "o" -> "<italic>"
                    "r" -> "<reset>"
                    else -> matchResult.value
                }
            }
    }

    fun getTextFromComponent(component: Component): String {
        return PlainTextComponentSerializer.plainText().serialize(component)
    }

    fun getColorfulTextFromComponent(component: Component): String {
        return mm.serialize(component)
    }

}