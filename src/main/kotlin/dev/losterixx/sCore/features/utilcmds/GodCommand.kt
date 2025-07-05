package dev.losterixx.sCore.features.utilcmds

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class GodCommand : CommandExecutor, TabCompleter {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission("sCore.command.god.self") && !sender.hasPermission("sCore.command.god.other")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        when (args.size) {
            0 -> {
                if (sender !is Player) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPlayer")))
                    return false
                }

                if (!sender.hasPermission("sCore.command.god.self")) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                    return false
                }

                sender.isInvulnerable = !sender.isInvulnerable
                if (sender.isInvulnerable) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.god.enabled-self")))
                } else {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.god.disabled-self")))
                }
            }

            1 -> {
                if (!sender.hasPermission("sCore.command.god.other")) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                    return false
                }

                if (args[0] == "*") {
                    Bukkit.getOnlinePlayers().forEach { player ->
                        player.isInvulnerable = !player.isInvulnerable
                        if (player.isInvulnerable) {
                            player.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.god.enabled-self")))
                        } else {
                            player.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.god.disabled-self")))
                        }
                    }
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.god.toggled-all")))
                } else {
                    val targetPlayer = main.server.getPlayer(args[0])

                    if (targetPlayer == null) {
                        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.playerNotFound")))
                        return false
                    }

                    targetPlayer.isInvulnerable = !targetPlayer.isInvulnerable
                    if (targetPlayer.isInvulnerable) {
                        targetPlayer.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.god.enabled-self")))
                        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.god.enabled-other")
                                    .replace("%player%", targetPlayer.name)))
                    } else {
                        targetPlayer.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.god.disabled-self")))
                        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.god.disabled-other")
                                    .replace("%player%", targetPlayer.name)))
                    }
                }
            }

            else -> {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.god.usage")))
            }
        }

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (!sender.hasPermission("sCore.command.god.other")) return completions

        if (args.isEmpty()) {
            completions.addAll(Bukkit.getOnlinePlayers().map { it.name })
            completions.add("*")
        } else if (args.size == 1) {
            completions.addAll(listOf("*") + Bukkit.getOnlinePlayers().map { it.name }
                .filter { it.startsWith(args[0], ignoreCase = true) })
        }

        return completions
    }

}