package dev.losterixx.sCore.other.updatechecker

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class UpdateListener : Listener {

    private val mm = Main.Companion.miniMessage
    private val main = Main.Companion.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.Companion.DEFAULT_PREFIX

    private val updateChecker = main.updateChecker

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        if (!getConfig().getBoolean("updateChecker.playerMessage")) return
        if (!player.hasPermission("sCore.admin")) return

        val currentVersion = main.description.version
        val latestVersion = updateChecker.getLatestGitHubRelease("Losterixx", "S-Core")

        if (latestVersion != null && latestVersion != currentVersion) {
            event.player.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("other.updateChecker")
                .replace("%latest-version%", latestVersion)
                .replace("%current-version%", currentVersion)))
        }
    }

}