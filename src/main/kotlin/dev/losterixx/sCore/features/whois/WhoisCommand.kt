package dev.losterixx.sCore.features.whois

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class WhoisCommand : CommandExecutor, TabCompleter {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission("sCore.command.whois")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.size != 1) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.whois.usage")))
            return false
        }

        val target = Bukkit.getPlayer(args[0])

        if (target == null || !target.isOnline) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.playerNotFound")))
            return false
        }

        for (message in getMessages().getStringList("commands.whois.messages")) {
            sender.sendMessage(mm.deserialize(
                PlaceholderAPI.setPlaceholders(target, message)
                .replace("%prefix%", getPrefix())))
        }

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (!sender.hasPermission("sCore.command.whois")) return completions

        if (args.isEmpty()) {
            Bukkit.getOnlinePlayers().forEach { player ->
                completions.add(player.name)
            }
        } else if (args.size == 1) {
            Bukkit.getOnlinePlayers().forEach { player ->
                if (player.name.startsWith(args[0], ignoreCase = true)) {
                    completions.add(player.name)
                }
            }
        }

        return completions
    }

}