package dev.losterixx.sCore.features.warps

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import io.papermc.paper.command.brigadier.argument.ArgumentTypes.player
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player


class WarpCommand : CommandExecutor, TabCompleter {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getData() = ConfigManager.getConfig("data")
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPlayer")))
            return false
        }

        if (!sender.hasPermission("sCore.command.warp")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.size != 1) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.warp.usage")))
            return false
        }

        val warpName = args[0]

        if (!getData().contains("warps.$warpName")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.warp.notFound")
                .replace("%warp%", warpName)))
            return false
        }

        val world = Bukkit.getWorld(getData().getString("warps.$warpName.world"))

        if (world == null) {
            sender.sendMessage(getPrefix() + mm.deserialize(getMessages().getString("commands.warp.locNotFound").replace("%warp%".toRegex(), warpName)))
            return false
        }

        val x = getData().getDouble("warps.$warpName.x")
        val y = getData().getDouble("warps.$warpName.y")
        val z = getData().getDouble("warps.$warpName.z")
        val yaw = getData().getDouble("warps.$warpName.yaw").toFloat()
        val pitch = getData().getDouble("warps.$warpName.pitch").toFloat()

        if (getConfig().getBoolean("sounds.teleport.enabled")) {
            val soundName = getConfig().getString("sounds.teleport.sound")
            val soundVolume = getConfig().getDouble("sounds.teleport.volume")
            val soundPitch = getConfig().getDouble("sounds.teleport.pitch")
            sender.playSound(sender, Sound.valueOf(soundName), SoundCategory.MASTER, soundVolume.toFloat(), soundPitch.toFloat())
        }

        val loc = Location(world, x, y, z, yaw, pitch)
        sender.teleport(loc)

        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.warp.teleport")
            .replace("%warp%", warpName)))

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (!sender.hasPermission("sCore.command.warp")) return completions
        if (getData().getSection("warps").getRoutesAsStrings(false).isEmpty()) return completions

        if (args.isEmpty()) {
            for (key in getData().getSection("warps").getRoutesAsStrings(false)) {
                completions.add(key)
            }
        } else if (args.size == 1) {
            for (key in getData().getSection("warps").getRoutesAsStrings(false)) {
                if (key.lowercase().startsWith(args[0].lowercase())) {
                    completions.add(key)
                }
            }
        }

        return completions
    }

}