package dev.losterixx.sCore.features.chat

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent
import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import dev.losterixx.sCore.utils.MMUtil
import io.papermc.paper.event.player.AsyncChatEvent
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatTabCompleteEvent

class ChatFeatureListener : Listener {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")

    /*@EventHandler(priority = EventPriority.LOWEST)
    fun onChat(event: AsyncChatEvent) {
        val player = event.player
        val messageText = MMUtil.translateLegacyCodes(MMUtil.getTextFromComponent(event.message()))
        val cleanedMessage = messageText.replace(Regex("<.*?>"), "")
        val messageWords = cleanedMessage.split(" ")
        var newMsg = messageText

        // Emojis
        if (getConfig().getBoolean("chat.features.emojis.enabled", false)) {
            val emojiSection = getConfig().getSection("chat.features.emojis.emojiMap") ?: return
            val emojiMap = emojiSection.getRoutesAsStrings(false).associateWith { emojiSection.getString(it) ?: "?" }.mapKeys { it.key }

            for (word in messageWords) {
                if (emojiMap.containsKey(word)) {
                    newMsg = newMsg.replace(word, emojiMap[word] ?: word)
                }
            }
        }

        event.message(mm.deserialize(newMsg))
    }*/

}
