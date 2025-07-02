package dev.losterixx.sCore.features.scoreboard

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard

object ScoreboardManager {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private val playerBoards = mutableMapOf<Player, Scoreboard>()

    fun startScoreboardUpdater() {
        if (!getConfig().getBoolean("scoreboard.enabled", true)) return
        object : BukkitRunnable() {
            override fun run() {
                Bukkit.getOnlinePlayers().forEach { updateScoreboard(it) }
            }
        }.runTaskTimer(main, 0L, getConfig().getLong("scoreboard.updateInterval", 20))
    }

    fun updateScoreboard(player: Player) {
        val scoreboard = playerBoards.getOrPut(player) { Bukkit.getScoreboardManager().newScoreboard }
        var objective = scoreboard.getObjective("sboard")
        val title = formatText(player, getConfig().getString("scoreboard.title", "Scoreboard") ?: "")

        if (objective == null) {
            objective = scoreboard.registerNewObjective("sboard", Criteria.create("dummy"), title)
            objective.displaySlot = DisplaySlot.SIDEBAR
        } else {
            objective.displayName(title)
        }

        val lines = getConfig().getStringList("scoreboard.lines")
        var score = lines.size

        for (line in lines) {
            val component = formatText(player, line)
            val entry = " ".repeat(score)
            val team = scoreboard.getTeam(entry) ?: scoreboard.registerNewTeam(entry)
            if (!team.hasEntry(entry)) team.addEntry(entry)
            team.prefix(component)
            objective.getScore(entry).score = score--
        }

        player.scoreboard = scoreboard
    }

    private fun formatText(player: Player, text: String): Component {
        val replaced = PlaceholderAPI.setPlaceholders(player, text)
        return mm.deserialize(replaced)
    }
}
