package dev.losterixx.sCore.features.gamemode

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent

class AutoGamemodeListener : Listener {

    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")


    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        if (!player.hasPlayedBefore()) {
            val gamemode = getConfig().getString("autoGamemode.onFirstJoin")
            if (gamemode != null && gamemode != "null") {
                main.server.scheduler.runTaskLater(main, Runnable {
                    player.gameMode = GameMode.valueOf(gamemode.uppercase())
                }, 5L)
            }
        } else {
            val gamemode = getConfig().getString("autoGamemode.onJoin")
            if (gamemode != null && gamemode != "null") {
                main.server.scheduler.runTaskLater(main, Runnable {
                    player.gameMode = GameMode.valueOf(gamemode.uppercase())
                }, 5L)
            }
        }
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        val gamemode = getConfig().getString("autoGamemode.onRespawn")

        if (gamemode != null && gamemode != "null") {
            main.server.scheduler.runTaskLater(main, Runnable {
                player.gameMode = GameMode.valueOf(gamemode.uppercase())
            }, 5L)
        }
    }

}
