package dev.losterixx.sCore.features.custom.customactions

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigActionHandler
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent

class CustomActionsListener : Listener {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        val actions = getConfig().getStringList("customActions.join", listOf())
        if (actions.isEmpty()) return

        ConfigActionHandler.executeActions(player, actions)
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player

        val actions = getConfig().getStringList("customActions.respawn", listOf())
        if (actions.isEmpty()) return

        ConfigActionHandler.executeActions(player, actions)
    }

}