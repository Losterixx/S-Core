package dev.losterixx.sCore.features.spawn

import dev.losterixx.sCore.Main
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetSpawnCommand : CommandExecutor {

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

        if (!sender.hasPermission("sCore.command.setspawn")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (!args.isEmpty()) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.setspawn.usage")))
            return false
        }

        val player = sender as Player
        val loc = player.location

        getData().set("spawn.world", loc.world?.name)
        getData().set("spawn.x", loc.x)
        getData().set("spawn.y", loc.y)
        getData().set("spawn.z", loc.z)
        getData().set("spawn.yaw", loc.yaw)
        getData().set("spawn.pitch", loc.pitch)
        configManager.saveConfig("data")

        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.setspawn.set")))

        return false
    }

}