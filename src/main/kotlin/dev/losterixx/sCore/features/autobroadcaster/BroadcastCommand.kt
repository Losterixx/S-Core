package dev.losterixx.sCore.features.autobroadcaster

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class BroadcastCommand : CommandExecutor, TabCompleter {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission("sCore.command.broadcast")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.isEmpty()) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.broadcast.usage")))
            return false
        }

        val messageId = args[0].lowercase()

        if (!getConfig().contains("autoBroadcaster.messages.$messageId")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.broadcast.notFound")
                .replace("%messageId%", messageId)))
            return false
        }

        BroadcastManager.broadcast(messageId)
        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.broadcast.sent")
            .replace("%messageId%", messageId)))

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (!sender.hasPermission("sCore.command.broadcast")) return completions

        if (args.isEmpty()) {
            for (key in getConfig().getSection("autoBroadcaster.messages").getRoutesAsStrings(false)) {
                completions.add(key)
            }
        } else if (args.size == 1) {
            for (key in getConfig().getSection("autoBroadcaster.messages").getRoutesAsStrings(false)) {
                if (key.lowercase().startsWith(args[0].lowercase())) {
                    completions.add(key)
                }
            }
        }

        return completions
    }

}