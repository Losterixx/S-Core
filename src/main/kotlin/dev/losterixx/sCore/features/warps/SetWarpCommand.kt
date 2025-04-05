package dev.losterixx.sCore.features.warps

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetWarpCommand : CommandExecutor {

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

        if (!sender.hasPermission("sCore.command.setwarp")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.size != 1) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.setwarp.usage")))
            return false
        }

        val warpName = args[0]

        if (getData().contains("warps.$warpName")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.setwarp.alreadyExists")
                .replace("%warp%", warpName)))
            return false
        }

        val loc = sender.location

        getData().set("warps.$warpName.world", loc.world?.name)
        getData().set("warps.$warpName.x", loc.x)
        getData().set("warps.$warpName.y", loc.y)
        getData().set("warps.$warpName.z", loc.z)
        getData().set("warps.$warpName.yaw", loc.yaw)
        getData().set("warps.$warpName.pitch", loc.pitch)
        ConfigManager.saveConfig("data")

        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.setwarp.set")
            .replace("%warp%", warpName)))

        return false
    }

}