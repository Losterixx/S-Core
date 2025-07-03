package dev.losterixx.sCore.features.nametag

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object NametagManager {

    private val mm: MiniMessage = Main.Companion.miniMessage
    private val main = Main.Companion.instance
    private fun getConfig() = ConfigManager.getConfig("config")

    fun startNametagUpdater() {
        object : BukkitRunnable() {
            override fun run() {
                if (!getConfig().getBoolean("nametag.enabled", true)) {
                    cancel()
                    return
                }

                Bukkit.getOnlinePlayers().forEach { player ->
                    updateNametag(player)
                }
            }
        }.runTaskTimer(main, 0L, getConfig().getLong("nametag.updateInterval", 20))
    }

    private fun updateNametag(player: Player) {
        val format = getConfig().getString("nametag.format") ?: "%player_name%"

        val playerName = player.name
        val prefixRaw = papiText(player, getConfig().getString("nametag.prefix", ""))
        val suffixRaw = papiText(player, getConfig().getString("nametag.suffix", ""))

        val prefix = mm.deserialize(prefixRaw)
        val suffix = mm.deserialize(suffixRaw)

        val scoreboard = player.scoreboard.takeIf { it != Bukkit.getScoreboardManager().mainScoreboard }
            ?: Bukkit.getScoreboardManager().newScoreboard.also { player.scoreboard = it }

        val teamName = "nametag_${player.uniqueId.toString().substring(0, 8)}"
        var team = scoreboard.getTeam(teamName)
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName)
        }

        team.prefix(prefix)
        team.suffix(suffix)

        if (!team.hasEntry(playerName)) {
            team.addEntry(playerName)
        }
    }

    private fun papiText(player: Player, text: String): String {
        return PlaceholderAPI.setPlaceholders(player, text)
    }
}