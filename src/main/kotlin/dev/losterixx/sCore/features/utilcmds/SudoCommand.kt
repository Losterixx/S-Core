package dev.losterixx.sCore.features.utilcmds

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import kotlin.text.startsWith

class SudoCommand : CommandExecutor, TabCompleter {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission("sCore.command.sudo")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.size < 2) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.sudo.usage")))
            return false
        }

        val targetPlayer = main.server.getPlayer(args[0])

        if (targetPlayer == null) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.playerNotFound")))
            return false
        }

        if (targetPlayer.hasPermission("sCore.bypass.sudo")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.sudo.bypass")))
            return false
        }

        val commandToExecute = args.drop(1).joinToString(" ")
        val isCommand = commandToExecute.startsWith("/")

        if (isCommand) {
            targetPlayer.performCommand(commandToExecute.removePrefix("/"))
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.sudo.executed")
                .replace("%player%", targetPlayer.name)
                .replace("%command%", commandToExecute)))
        } else {
            targetPlayer.chat(commandToExecute)
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.sudo.messageSent")
                .replace("%player%", targetPlayer.name)
                .replace("%message%", commandToExecute)))
        }

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (!sender.hasPermission("sCore.command.sudo")) return completions

        if (args.isEmpty()) {
            completions.addAll(Bukkit.getOnlinePlayers().filter { !it.hasPermission("sCore.bypass.sudo") }.map { it.name })
            completions.add("*")
        } else if (args.size == 1) {
            completions.addAll(listOf("*") + Bukkit.getOnlinePlayers().filter { !it.hasPermission("sCore.bypass.sudo") }.map { it.name }
                .filter { it.startsWith(args[0], ignoreCase = true) })
        }

        return completions
    }

}