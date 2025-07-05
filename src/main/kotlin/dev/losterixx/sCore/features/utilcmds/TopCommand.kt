package dev.losterixx.sCore.features.utilcmds

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TopCommand : CommandExecutor {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPlayer")))
            return false
        }

        if (!sender.hasPermission("sCore.command.top")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (!args.isEmpty()) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.top.usage")))
            return false
        }

        val loc = sender.location
        val newLoc = loc.also { it.y = loc.world.getHighestBlockAt(loc).y.toDouble() + 1.0 }

        sender.teleport(newLoc)
        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.top.teleported")))

        return false
    }

}