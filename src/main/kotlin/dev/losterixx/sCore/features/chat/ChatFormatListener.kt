package dev.losterixx.sCore.features.chat

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import io.papermc.paper.event.player.AsyncChatEvent
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatFormatListener : Listener {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val player = event.player

        if (!getConfig().getBoolean("chat.format.enabled")) return

        var messageText = translateLegacyCodes(PlainTextComponentSerializer.plainText().serialize(event.message()))
        val rawFormat = getConfig().getString("chat.format.format") ?: return

        val hasColor = player.hasPermission("sCore.chat.color")
        val hasFormat = player.hasPermission("sCore.chat.format")
        val hasMiniMessage = player.hasPermission("sCore.chat.minimessage")

        if (!hasMiniMessage) {
            val allowedTags = listOf(
                "black","dark_blue","dark_green","dark_aqua","dark_red","dark_purple","gold","gray","dark_gray",
                "blue","green","aqua","red","light_purple","yellow","white",
                "bold", "b", "italic", "i", "underlined", "u", "strikethrough", "obfuscated"
            )
            val allowedPattern = allowedTags.joinToString("|")
            messageText = messageText.replace(
                Regex("<(?!$allowedPattern)([a-zA-Z0-9_:-]+)(:[^>]+)?\\s*\\/?>", RegexOption.IGNORE_CASE),
                ""
            )
            messageText = messageText.replace(
                Regex("</(?!$allowedPattern)[a-zA-Z0-9_:-]+>", RegexOption.IGNORE_CASE),
                ""
            )
        }
        if (!hasFormat) {
            messageText = messageText.replace(Regex("<(bold|b|italic|i|underlined|u|strikethrough|obfuscated)>", RegexOption.IGNORE_CASE), "")
            messageText = messageText.replace(Regex("</(bold|b|italic|i|underlined|u|strikethrough|obfuscated)>", RegexOption.IGNORE_CASE), "")
        }
        if (!hasColor) {
            messageText = messageText.replace(Regex("<(black|dark_blue|dark_green|dark_aqua|dark_red|dark_purple|gold|gray|dark_gray|blue|green|aqua|red|light_purple|yellow|white)>", RegexOption.IGNORE_CASE), "")
            messageText = messageText.replace(Regex("</(black|dark_blue|dark_green|dark_aqua|dark_red|dark_purple|gold|gray|dark_gray|blue|green|aqua|red|light_purple|yellow|white)>", RegexOption.IGNORE_CASE), "")
        }

        val parsedFormat = PlaceholderAPI.setPlaceholders(player, rawFormat).replace("%message%", messageText)

        event.isCancelled = true
        val component = mm.deserialize(parsedFormat)
        main.server.broadcast(component)
    }

    private fun translateLegacyCodes(message: String): String {
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
}