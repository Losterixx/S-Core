package dev.losterixx.sCore.features.warps

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ListWarpsCommand : CommandExecutor {

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

        if (!sender.hasPermission("sCore.command.listwarps")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.isNotEmpty()) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.listwarps.usage")))
            return false
        }

        val warpsSection = getData().getSection("warps")
        if (warpsSection == null || warpsSection.getRoutesAsStrings(false).isEmpty()) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.listwarps.noWarps")))
            return false
        }

        sender.sendMessage(mm.deserialize(getMessages().getString("commands.listwarps.warpsHeader")
            .replace("%prefix%", getPrefix())))

        for (warp in warpsSection.getRoutesAsStrings(false)) {
            val line = getMessages().getString("commands.listwarps.warpsElement")!!
                .replace("%prefix%", getPrefix())
                .replace("%warp%", warp)
            sender.sendMessage(mm.deserialize(line))
        }

        sender.sendMessage(mm.deserialize(getMessages().getString("commands.listwarps.warpsFooter")
            .replace("%prefix%", getPrefix())))

        return false
    }

}