package dev.losterixx.sCore.features.utilcmds

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class HatCommand : CommandExecutor {

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

        if (!sender.hasPermission("sCore.command.hat")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (!args.isEmpty()) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.hat.usage")))
            return false
        }

        val handItem = sender.inventory.itemInMainHand

        if (handItem == null || handItem.type.isAir) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.hat.noItem")))
            return false
        }

        if (sender.inventory.helmet != null && sender.inventory.helmet?.type?.isAir == false) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.hat.alreadyWearing")))
            return false
        }

        sender.inventory.helmet = handItem
        sender.inventory.setItemInMainHand(null)
        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.hat.equipped")))

        return false
    }

}