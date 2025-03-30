package dev.losterixx.sCore.features.custommessages

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class CustomMessagesListener : Listener {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val joinMessage = getMessages().getString("customMessages.join.message", null)

        if (getMessages().getBoolean("customMessages.join.enabled")) {
            val message = if (joinMessage != null) mm.deserialize(joinMessage
                .replace("%prefix%", getPrefix())
                .replace("%player%", player.name))
            else null
            event.joinMessage(message)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val quitMessage = getMessages().getString("customMessages.quit.message", null)

        if (getMessages().getBoolean("customMessages.quit.enabled")) {
            val message = if (quitMessage != null) mm.deserialize(quitMessage
                .replace("%prefix%", getPrefix())
                .replace("%player%", player.name))
            else null
            event.quitMessage(message)
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.player
        val deathMessage = getMessages().getString("customMessages.death.message", null)

        if (getMessages().getBoolean("customMessages.death.enabled")) {
            val message = if (deathMessage != null) mm.deserialize(deathMessage
                .replace("%prefix%", getPrefix())
                .replace("%player%", player.name))
            else null
            event.deathMessage(message)
        }
    }

}