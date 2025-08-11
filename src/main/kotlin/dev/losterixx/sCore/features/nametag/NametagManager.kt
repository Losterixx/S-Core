package dev.losterixx.sCore.features.nametag

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object NametagManager {

    private val mm: MiniMessage = Main.miniMessage
    private val main = Main.instance
    private val lp = main.luckperms
    private fun getConfig() = ConfigManager.getConfig("config")

    fun startNametagUpdater() {
        object : BukkitRunnable() {
            override fun run() {
                if (!getConfig().getBoolean("nametag.enabled", true)) {
                    cancel()
                    return
                }
                updateAllNametags()
            }
        }.runTaskTimer(main, 0L, getConfig().getLong("nametag.updateInterval", 20))
    }

    private fun updateAllNametags() {
        val onlinePlayers = Bukkit.getOnlinePlayers()
        for (viewer in onlinePlayers) {
            val scoreboard = viewer.scoreboard.takeIf { it != Bukkit.getScoreboardManager().mainScoreboard }
                ?: Bukkit.getScoreboardManager().newScoreboard.also { viewer.scoreboard = it }

            for (target in onlinePlayers) {
                val weight = getGroupValue(target).coerceIn(0, 9999)
                val invertedWeight = 9999 - weight
                val teamName = "${"%04d".format(invertedWeight)}_${target.uniqueId.toString().substring(0, 8)}"

                val prefixRaw = papiText(target, getConfig().getString("nametag.prefix", ""))
                val suffixRaw = papiText(target, getConfig().getString("nametag.suffix", ""))
                val prefix = mm.deserialize(prefixRaw)
                val suffix = mm.deserialize(suffixRaw)

                var team = scoreboard.getTeam(teamName)
                if (team == null) {
                    team = scoreboard.registerNewTeam(teamName)
                }

                team.prefix(prefix)
                team.suffix(suffix)

                scoreboard.teams.filter { it.hasEntry(target.name) && it != team }.forEach { it.removeEntry(target.name) }

                if (!team.hasEntry(target.name)) {
                    team.addEntry(target.name)
                }
            }
        }
    }

    private fun papiText(player: Player, text: String): String {
        return PlaceholderAPI.setPlaceholders(player, PlaceholderAPI.setPlaceholders(player, text))
    }

    private fun getGroupValue(player: Player): Int {
        if (lp == null) return 0
        val user = lp.userManager.getUser(player.uniqueId) ?: return 0
        val primaryGroup = user.primaryGroup
        val group = lp.groupManager.getGroup(primaryGroup) ?: return 0
        val meta = group.cachedData.metaData
        return meta.weight ?: 0
    }
}
