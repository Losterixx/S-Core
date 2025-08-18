package dev.losterixx.sCore.features.chat

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager

object ChatUtils {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")

    fun replaceEmojis(message: String): String {
        if (!getConfig().getBoolean("chat.features.emojis.enabled", false)) return message

        val emojiSection = getConfig().getSection("chat.features.emojis.emojiMap") ?: return message
        val emojiMap = emojiSection.getRoutesAsStrings(false).associateWith { emojiSection.getString(it) ?: "?" }

        val enableHover = getConfig().getBoolean("chat.features.emojis.enableHoverKey", true)

        return message.split(" ").joinToString(" ") { word ->
            if (emojiMap.containsKey(word)) {
                val emoji = emojiMap[word] ?: word
                if (enableHover) {
                    "<hover:show_text:\"<white>$word\">$emoji</hover>"
                } else {
                    emoji
                }
            } else {
                word
            }
        }
    }

}