package dev.losterixx.sCore.features.utilcmds

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class FeedCommand : CommandExecutor, TabCompleter {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission("sCore.command.feed.self") && !sender.hasPermission("sCore.command.feed.other")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        when (args.size) {
            0 -> {
                if (sender !is Player) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPlayer")))
                    return false
                }

                if (!sender.hasPermission("sCore.command.feed.self")) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                    return false
                }

                sender.foodLevel = 20
                sender.saturation = 20f
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.feed.feed-self")))
            }

            1 -> {
                if (!sender.hasPermission("sCore.command.feed.other")) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                    return false
                }

                if (args[0] == "*") {
                    Bukkit.getOnlinePlayers().forEach { player ->
                        player.foodLevel = 20
                        player.saturation = 20f
                        player.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.feed.feed-self")))
                    }
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.feed.feed-all")))
                } else {
                    val targetPlayer = main.server.getPlayer(args[0])

                    if (targetPlayer == null) {
                        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.playerNotFound")))
                        return false
                    }

                    targetPlayer.foodLevel = 20
                    targetPlayer.saturation = 20f
                    targetPlayer.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.feed.feed-self")))
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.feed.feed-other")
                                .replace("%player%", targetPlayer.name)))
                }
            }

            else -> {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.feed.usage")))
            }
        }

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (!sender.hasPermission("sCore.command.feed.other")) return completions

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