package dev.losterixx.sCore.features.gamemode

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*


class GamemodeCommand : CommandExecutor, TabCompleter {

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

        if (!sender.hasPermission("sCore.command.gamemode.survival.self") && !sender.hasPermission("sCore.command.gamemode.creative.self") && !sender.hasPermission("sCore.command.gamemode.adventure.self") && !sender.hasPermission("sCore.command.gamemode.spectator.self")
            && !sender.hasPermission("sCore.command.gamemode.survival.other") && !sender.hasPermission("sCore.command.gamemode.creative.other") && !sender.hasPermission("sCore.command.gamemode.adventure.other") && !sender.hasPermission("sCore.command.gamemode.spectator.other")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.isEmpty() || args.size > 2) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.gamemode.usage")))
            return false
        }

        when(args[0].lowercase()) {

            "survival", "s", "0" -> {
                if (args.size == 1) {
                    if (!sender.hasPermission("sCore.command.gamemode.survival.self")) {
                        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                        return false
                    }

                    sender.gameMode = GameMode.SURVIVAL
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.gamemode.changed-self")
                        .replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.survival"))))
                } else if (args.size == 2) {
                    if (!sender.hasPermission("sCore.command.gamemode.survival.other")) {
                        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                        return false
                    }

                    val target = Bukkit.getPlayer(args[1])
                    if (target == null || !target.isOnline) {
                        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.playerNotFound")))
                        return false
                    }

                    target.gameMode = GameMode.SURVIVAL
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.gamemode.changed-other")
                        .replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.survival"))
                        .replace("%player%", target.name)))
                    target.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.gamemode.changed-self")
                        .replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.survival"))))
                }
            }

            "creative", "c", "1" -> {
                if (args.size == 1) {
                    if (!sender.hasPermission("sCore.command.gamemode.creative.self")) {
                        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                        return false
                    }

                    sender.gameMode = GameMode.CREATIVE
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.gamemode.changed-self")
                        .replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.creative"))))
                } else if (args.size == 2) {
                    if (!sender.hasPermission("sCore.command.gamemode.creative.other")) {
                        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                        return false
                    }

                    val target = Bukkit.getPlayer(args[1])
                    if (target == null || !target.isOnline) {
                        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.playerNotFound")))
                        return false
                    }

                    target.gameMode = GameMode.CREATIVE
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.gamemode.changed-other")
                        .replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.creative"))
                        .replace("%player%", target.name)))
                    target.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.gamemode.changed-self")
                        .replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.creative"))))
                }
            }

            "adventure", "a", "2" -> {
                if (args.size == 1) {
                    if (!sender.hasPermission("sCore.command.gamemode.adventure.self")) {
                        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                        return false
                    }

                    sender.gameMode = GameMode.ADVENTURE
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.gamemode.changed-self")
                        .replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.adventure"))))
                } else if (args.size == 2) {
                    if (!sender.hasPermission("sCore.command.gamemode.adventure.other")) {
                        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                        return false
                    }

                    val target = Bukkit.getPlayer(args[1])
                    if (target == null || !target.isOnline) {
                        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.playerNotFound")))
                        return false
                    }

                    target.gameMode = GameMode.ADVENTURE
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.gamemode.changed-other")
                        .replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.adventure"))
                        .replace("%player%", target.name)))
                    target.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.gamemode.changed-self")
                        .replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.adventure"))))
                }
            }

            "spectator", "spec", "sp", "3" -> {
                if (args.size == 1) {
                    if (!sender.hasPermission("sCore.command.gamemode.spectator.self")) {
                        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                        return false
                    }

                    sender.gameMode = GameMode.SPECTATOR
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.gamemode.changed-self")
                        .replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.spectator"))))
                } else if (args.size == 2) {
                    if (!sender.hasPermission("sCore.command.gamemode.spectator.other")) {
                        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                        return false
                    }

                    val target = Bukkit.getPlayer(args[1])
                    if (target == null || !target.isOnline) {
                        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.playerNotFound")))
                        return false
                    }

                    target.gameMode = GameMode.SPECTATOR
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.gamemode.changed-other")
                        .replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.spectator"))
                        .replace("%player%", target.name)))
                    target.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.gamemode.changed-self")
                        .replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.spectator"))))
                }
            }

            else -> {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.gamemode.usage")))
            }

        }

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (args.isEmpty()) {
            if (sender.hasPermission("sCore.command.gamemode.survival.self") || sender.hasPermission("sCore.command.gamemode.survival.other")) completions.add("survival")
            if (sender.hasPermission("sCore.command.gamemode.creative.self") || sender.hasPermission("sCore.command.gamemode.creative.other")) completions.add("creative")
            if (sender.hasPermission("sCore.command.gamemode.adventure.self") || sender.hasPermission("sCore.command.gamemode.adventure.other")) completions.add("adventure")
            if (sender.hasPermission("sCore.command.gamemode.spectator.self") || sender.hasPermission("sCore.command.gamemode.spectator.other")) completions.add("spectator")
        } else if (args.size == 1) {
            if ("survival".startsWith(args[0].lowercase()) && (sender.hasPermission("sCore.command.gamemode.survival.self") || sender.hasPermission("sCore.command.gamemode.survival.other"))) completions.add("survival")
            if ("creative".startsWith(args[0].lowercase()) && (sender.hasPermission("sCore.command.gamemode.creative.self") || sender.hasPermission("sCore.command.gamemode.creative.other"))) completions.add("creative")
            if ("adventure".startsWith(args[0].lowercase()) && (sender.hasPermission("sCore.command.gamemode.adventure.self") || sender.hasPermission("sCore.command.gamemode.adventure.other"))) completions.add("adventure")
            if ("spectator".startsWith(args[0].lowercase()) && (sender.hasPermission("sCore.command.gamemode.spectator.self") || sender.hasPermission("sCore.command.gamemode.spectator.other"))) completions.add("spectator")
        } else if (args.size == 2) {
            if (!sender.hasPermission("sCore.command.gamemode.survival.other") && !sender.hasPermission("sCore.command.gamemode.creative.other") && !sender.hasPermission("sCore.command.gamemode.adventure.other") && !sender.hasPermission("sCore.command.gamemode.spectator.other")) return completions

            for (player in Bukkit.getOnlinePlayers()) {
                if (player.name.lowercase(Locale.getDefault()).startsWith(args[1].lowercase())) completions.add(player.name)
            }
        }

        return completions
    }

}