package dev.losterixx.sCore.features.tablist

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object TablistManager {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")

    fun startTablistUpdater() {
        object : BukkitRunnable() {
            override fun run() {
                if (!getConfig().getBoolean("tablist.enabled", true)) {
                    cancel()
                    return
                }
                Bukkit.getOnlinePlayers().forEach { updateTablist(it) }
            }
        }.runTaskTimer(main, 0L, getConfig().getLong("tablist.updateInterval", 20))
    }

    private fun updateTablist(player: Player) {
        val headerStr = getConfig().getStringList("tablist.header").joinToString("<newline>")
        val footerStr = getConfig().getStringList("tablist.footer").joinToString("<newline>")
        val headerComponent = formatTablistText(player, headerStr)
        val footerComponent = formatTablistText(player, footerStr)
        player.sendPlayerListHeaderAndFooter(headerComponent, footerComponent)
    }

    private fun formatTablistText(player: Player, text: String): Component {
        val result = if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PlaceholderAPI.setPlaceholders(player, text)
        } else {
            text
        }
        return mm.deserialize(result)
    }
}