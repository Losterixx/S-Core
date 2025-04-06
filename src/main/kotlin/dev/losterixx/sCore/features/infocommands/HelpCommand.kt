package dev.losterixx.sCore.features.infocommands

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import kotlin.system.measureTimeMillis

class HelpCommand  : CommandExecutor {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission("sCore.command.help")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.isNotEmpty()) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.help.usage")))
            return false
        }

        for (message in getMessages().getStringList("commands.help.messages")) {
            sender.sendMessage(mm.deserialize(message
                .replace("%prefix%", getPrefix()
                .replace("%player%", sender.name))))
        }

        return false
    }

}