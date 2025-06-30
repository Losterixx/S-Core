package dev.losterixx.sCore.features.chat

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.Main.Companion.miniMessage
import dev.losterixx.sCore.utils.ConfigManager
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ChatLockCommand : CommandExecutor, Listener {

    private val mm = miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission("sCore.command.chatlock")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (!args.isEmpty()) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.chatlock.usage")))
            return false
        }

        val isChatLocked = getConfig().getBoolean("chat.chatlock.isChatLocked", false)

        if (isChatLocked) {
            getConfig().set("chat.chatlock.isChatLocked", false)
            ConfigManager.saveConfig("config")
            main.server.broadcast(mm.deserialize(getPrefix() + getMessages().getString("commands.chatlock.unlocked")))
        } else {
            getConfig().set("chat.chatlock.isChatLocked", true)
            ConfigManager.saveConfig("config")
            main.server.broadcast(mm.deserialize(getPrefix() + getMessages().getString("commands.chatlock.locked")))
        }

        return false
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onChat(event: AsyncChatEvent) {
        val player = event.player
        if (!getConfig().getBoolean("chat.chatlock.isChatLocked", false)) return

        if (!player.hasPermission("sCore.bypass.chatlock")) {
            player.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.chatlock.chatLocked")))
            event.isCancelled = true
        }
    }
}