package dev.losterixx.sCore.features.utilcmds

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class FlyCommand : CommandExecutor, TabCompleter {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission("sCore.command.fly.self") && !sender.hasPermission("sCore.command.fly.other")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        when (args.size) {
            0 -> {
                if (sender !is Player) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPlayer")))
                    return false
                }

                if (!sender.hasPermission("sCore.command.fly.self")) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                    return false
                }

                sender.allowFlight = !sender.allowFlight
                if (sender.allowFlight) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.fly.enabled-self")))
                } else {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.fly.disabled-self")))
                }
            }

            1 -> {
                if (!sender.hasPermission("sCore.command.fly.other")) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                    return false
                }

                val targetPlayer = main.server.getPlayer(args[0])

                if (targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.playerNotFound")))
                    return false
                }

                targetPlayer.allowFlight = !targetPlayer.allowFlight
                if (targetPlayer.allowFlight) {
                    targetPlayer.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.fly.enabled-self")))
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.fly.enabled-other")
                        .replace("%player%", targetPlayer.name)))
                } else {
                    targetPlayer.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.fly.disabled-self")))
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.fly.disabled-other")
                        .replace("%player%", targetPlayer.name)))
                }
            }

            else -> {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.fly.usage")))
            }
        }

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (!sender.hasPermission("sCore.command.fly.other")) return completions

        if (args.isEmpty()) {
            completions.addAll(Bukkit.getOnlinePlayers().map { it.name })
        } else if (args.size == 1) {
            completions.addAll(Bukkit.getOnlinePlayers().map { it.name }
                .filter { it.startsWith(args[0], ignoreCase = true) })
        }

        return completions
    }

}