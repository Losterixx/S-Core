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

        val messageText = PlainTextComponentSerializer.plainText().serialize(event.message())
        val rawFormat = getConfig().getString("chat.format.format") ?: return
        val parsedFormat = if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PlaceholderAPI.setPlaceholders(player,
            rawFormat.replace("%message%", messageText)
            )
        } else {
            rawFormat
                .replace("%player_name%", player.name)
                .replace("%message%", messageText)
        }

        event.isCancelled = true
        val component = mm.deserialize(parsedFormat)
        main.server.broadcast(component)
    }

}