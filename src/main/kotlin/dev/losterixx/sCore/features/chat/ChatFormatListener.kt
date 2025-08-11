package dev.losterixx.sCore.features.chat

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import dev.losterixx.sCore.utils.MMUtil
import io.papermc.paper.event.player.AsyncChatEvent
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ChatFormatListener : Listener {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onChat(event: AsyncChatEvent) {
        val player = event.player
        if (!getConfig().getBoolean("chat.format.enabled") || event.isCancelled) return

        var messageText = MMUtil.translateLegacyCodes(MMUtil.getColorfulTextFromComponent(event.message()))

        val rawFormat = getConfig().getString("chat.format.format")?.replace("%message%", "<message>") ?: return

        val hasColor = player.hasPermission("sCore.chat.color")
        val hasFormat = player.hasPermission("sCore.chat.format")
        val hasMiniMessage = player.hasPermission("sCore.chat.minimessage")

        if (!hasMiniMessage) {
            val allowedTags = listOf(
                "black","dark_blue","dark_green","dark_aqua","dark_red","dark_purple","gold","gray","dark_gray",
                "blue","green","aqua","red","light_purple","yellow","white",
                "bold","b","italic","i","underlined","u","strikethrough","obfuscated"
            )
            val allowedPattern = allowedTags.joinToString("|")
            messageText = messageText.replace(
                Regex("<(?!$allowedPattern)([a-zA-Z0-9_:-]+)(:[^>]+)?\\s*\\/?>", RegexOption.IGNORE_CASE),
                ""
            ).replace(
                Regex("</(?!$allowedPattern)[a-zA-Z0-9_:-]+>", RegexOption.IGNORE_CASE),
                ""
            )
        }
        if (!hasFormat) {
            messageText = messageText.replace(Regex("<(bold|b|italic|i|underlined|u|strikethrough|obfuscated)>", RegexOption.IGNORE_CASE), "")
                .replace(Regex("</(bold|b|italic|i|underlined|u|strikethrough|obfuscated)>", RegexOption.IGNORE_CASE), "")
        }
        if (!hasColor) {
            messageText = messageText.replace(Regex("<(black|dark_blue|dark_green|dark_aqua|dark_red|dark_purple|gold|gray|dark_gray|blue|green|aqua|red|light_purple|yellow|white)>", RegexOption.IGNORE_CASE), "")
                .replace(Regex("</(black|dark_blue|dark_green|dark_aqua|dark_red|dark_purple|gold|gray|dark_gray|blue|green|aqua|red|light_purple|yellow|white)>", RegexOption.IGNORE_CASE), "")
        }

        val parsedFormat = PlaceholderAPI.setPlaceholders(player, rawFormat)

        val messageComponent: Component = mm.deserialize(messageText)

        event.isCancelled = true

        val finalComponent = mm.deserialize(parsedFormat, Placeholder.component("message", messageComponent))

        main.server.broadcast(finalComponent)
    }
}
