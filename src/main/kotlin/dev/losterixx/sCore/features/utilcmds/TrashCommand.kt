package dev.losterixx.sCore.features.utilcmds

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.metadata.FixedMetadataValue

class TrashCommand : CommandExecutor, Listener {

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

        if (!sender.hasPermission("sCore.command.trash")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (!args.isEmpty()) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.trash.usage")))
            return false
        }

        val gui = Bukkit.createInventory(null, getMessages().getInt("commands.trash.guiRows", 2) * 9, mm.deserialize(getMessages().getString("commands.trash.guiTitle", "Trash")))
        sender.openInventory(gui)
        sender.setMetadata("score_gui_trash", FixedMetadataValue(main, true))

        return false
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as Player? ?: return
        if (player.hasMetadata("score_gui_trash")) {
            player.removeMetadata("score_gui_trash", main)
            player.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.trash.deleted")))
        }
    }

}