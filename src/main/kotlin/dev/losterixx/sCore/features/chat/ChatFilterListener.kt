package dev.losterixx.sCore.features.chat

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import dev.losterixx.sCore.utils.MMUtil
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

class ChatFilterListener : Listener {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX
    private val lastMessages = mutableMapOf<UUID, String>()
    private val lastMessagesTimestamp = mutableMapOf<UUID, Long>()

    @EventHandler(priority = EventPriority.LOWEST)
    fun onChat(event: AsyncChatEvent) {
        val player = event.player
        val messageText = MMUtil.translateLegacyCodes(MMUtil.getTextFromComponent(event.message()))
        val cleanedMessage = messageText.replace(Regex("<.*?>"), "")
        val messageWords = cleanedMessage.split(" ")

        if (player.hasPermission("sCore.bypass.chatfilter")) {
            updateMaps(player.uniqueId, cleanedMessage)
            return
        }

        val now = System.currentTimeMillis()

        // Delay Check
        if (getConfig().getBoolean("chat.chatfilter.chatDelay.enabled", false)) {
            val lastTimestamp = lastMessagesTimestamp[player.uniqueId]
            val delayMs = getConfig().getLong("chat.chatfilter.chatDelay.delay", 3) * 1000
            if (lastTimestamp != null && now - lastTimestamp < delayMs) {
                player.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("chat.chatfilter.delayMessage")))
                event.isCancelled = true
                return
            }
        }

        // No Empty Messages
        if (getConfig().getBoolean("chat.chatfilter.noEmptyMessages", false) && cleanedMessage.isEmpty()) {
            player.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("chat.chatfilter.emptyMessage")))
            event.isCancelled = true
            return
        }

        // Anti Caps
        if (getConfig().getBoolean("chat.chatfilter.antiCaps.enabled", false) && messageText.length > getConfig().getInt("chat.chatfilter.antiCaps.minLength", 7)) {
            val uppercases = messageText.count { it.isUpperCase() }
            val lowercases = messageText.count { it.isLowerCase() }
            val percentage = getConfig().getInt("chat.chatfilter.antiCaps.percentage", 50) / 100.0
            if (uppercases + lowercases > 0 && uppercases.toDouble() / (uppercases + lowercases) > percentage) {
                player.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("chat.chatfilter.capsMessage")))
                event.isCancelled = true
                return
            }
        }

        // Anti Spam
        if (getConfig().getBoolean("chat.chatfilter.antiSpam", false)) {
            val lastMessage = lastMessages[player.uniqueId]
            if (lastMessage == cleanedMessage) {
                player.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("chat.chatfilter.spamMessage")))
                event.isCancelled = true
                return
            }
        }

        // Block Invalid Characters
        if (getConfig().getBoolean("chat.chatfilter.blockInvalidCharacters.enabled", false)) {
            val allowedChars = Regex(getConfig().getString("chat.chatfilter.blockInvalidCharacters.allowedCharsRegex", "^[\\\\p{L}\\\\p{N} ,;.:_#'+~´`?=(){}\\[\\]&%$\\\"²³!^°<>|€@\\\\-]*$"))
            if (!cleanedMessage.matches(allowedChars)) {
                player.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("chat.chatfilter.invalidCharacters")))
                event.isCancelled = true
                return
            }
        }

        // Anti Swear
        if (getConfig().getBoolean("chat.chatfilter.antiSwear.enabled", false)) {
            val swearWords = getConfig().getStringList("chat.chatfilter.antiSwear.blockedWords")
            val ignoreCase = getConfig().getBoolean("chat.chatfilter.antiSwear.ignoreCase", true)
            val checkWords = if (ignoreCase) swearWords.map { it.lowercase() } else swearWords
            val wordsToCheck = if (ignoreCase) messageWords.map { it.lowercase() } else messageWords

            for (word in wordsToCheck) {
                if (word in checkWords) {
                    player.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("chat.chatfilter.swearMessage")))
                    event.isCancelled = true
                    return
                }
            }
        }

        updateMaps(player.uniqueId, cleanedMessage)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        lastMessages.remove(event.player.uniqueId)
        lastMessagesTimestamp.remove(event.player.uniqueId)
    }

    private fun updateMaps(uuid: UUID, message: String) {
        lastMessages[uuid] = message
        lastMessagesTimestamp[uuid] = System.currentTimeMillis()
    }
}