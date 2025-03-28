package dev.losterixx.sCore.features.spawn

import dev.losterixx.sCore.Main
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SpawnCommand : CommandExecutor {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private val configManager = Main.configManager
    private fun getConfig() = configManager.getConfig("config")
    private fun getMessages() = configManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getData() = configManager.getConfig("data")
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPlayer")))
            return false
        }

        if (!sender.hasPermission("sCore.command.spawn")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (!args.isEmpty()) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.spawn.usage")))
            return false
        }

        val player = sender as Player
        val worldString = getData().getString("spawn.world", null)

        if (worldString == null) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.spawn.noSpawnSet")))
            return false
        }

        val world = Bukkit.getWorld(worldString)
        val x = getData().getDouble("spawn.x", 0.0)
        val y = getData().getDouble("spawn.y", 0.0)
        val z = getData().getDouble("spawn.z", 0.0)
        val yaw = getData().getFloat("spawn.yaw", 0.0f)
        val pitch = getData().getFloat("spawn.pitch", 0.0f)

        val loc = Location(world, x, y, z, yaw, pitch)

        if (getMessages().getBoolean("commands.spawn.sound.enabled")) {
            val soundName = getMessages().getString("commands.spawn.sound.sound")
            val soundVolume = getMessages().getDouble("commands.spawn.sound.volume")
            val soundPitch = getMessages().getDouble("commands.spawn.sound.pitch")
            player.playSound(player, Sound.valueOf(soundName), SoundCategory.MASTER, soundVolume.toFloat(), soundPitch.toFloat())
        }

        player.teleport(loc)
        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.spawn.teleport")))

        return false
    }

}