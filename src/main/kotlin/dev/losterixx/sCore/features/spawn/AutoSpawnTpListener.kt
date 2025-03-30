package dev.losterixx.sCore.features.spawn

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent

class AutoSpawnTpListener : Listener {

    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getData() = ConfigManager.getConfig("data")

    private fun teleportToSpawn(player: Player) {
        getData().getString("spawn.world")?.let { worldName ->
            Bukkit.getWorld(worldName)?.let { world ->
                val loc = Location(
                    world,
                    getData().getDouble("spawn.x", 0.0),
                    getData().getDouble("spawn.y", 0.0),
                    getData().getDouble("spawn.z", 0.0),
                    getData().getFloat("spawn.yaw", 0.0f),
                    getData().getFloat("spawn.pitch", 0.0f)
                )
                player.teleport(loc)
            }
        }
    }


    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val path = if (player.hasPlayedBefore()) "autoSpawnTeleport.onJoin" else "autoSpawnTeleport.onFirstJoin"

        if (getConfig().getBoolean(path, false)) {
            main.server.scheduler.runTaskLater(main, Runnable {
                teleportToSpawn(player)
            }, 5L)
        }
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player

        if (getConfig().getBoolean("autoSpawnTeleport.onRespawn", false)) {
            main.server.scheduler.runTaskLater(main, Runnable {
                teleportToSpawn(player)
            }, 5L)
        }
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player

        if (getConfig().getBoolean("autoSpawnTeleport.onMove", false)) {
            teleportToSpawn(player)
        }
    }
}
