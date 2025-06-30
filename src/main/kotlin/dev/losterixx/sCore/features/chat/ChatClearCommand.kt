package dev.losterixx.sCore.features.chat

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.Main.Companion.miniMessage
import dev.losterixx.sCore.utils.ConfigManager
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ChatClearCommand : CommandExecutor {

    private val mm = miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission("sCore.command.chatclear")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (!args.isEmpty()) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.chatclear.usage")))
            return false
        }

        val emptyMessages = getConfig().getInt("chat.chatclear.emptyMessages", 100)
        for (i in 1..emptyMessages) {
            main.server.broadcast(Component.empty())
        }

        getMessages().getStringList("commands.chatclear.cleared").forEach { message ->
            main.server.broadcast(mm.deserialize(message
                .replace("%prefix%", getPrefix())
                .replace("%player%", sender.name)))
        }

        return false
    }

}