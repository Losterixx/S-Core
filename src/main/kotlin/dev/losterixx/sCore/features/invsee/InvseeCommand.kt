package dev.losterixx.sCore.features.invsee

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import dev.losterixx.sCore.utils.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.metadata.FixedMetadataValue

class InvseeCommand : CommandExecutor, TabCompleter, Listener {

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

        if (!sender.hasPermission("sCore.command.invsee")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.size != 1) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.invsee.usage")))
            return false
        }

        val player = sender as Player
        val target = Bukkit.getPlayer(args[0])

        if (target == null || !target.isOnline) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.playerNotFound")))
            return false
        }

        if (target.uniqueId == player.uniqueId) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.invsee.selfError")))
            return false
        }

        val gui = Bukkit.createInventory(null, 6 * 9, mm.deserialize(getConfig().getString("invsee.gui.title", "Error").replace("%player%", target.name)))

        gui.contents = target.inventory.contents

        gui.setItem(46, target.inventory.helmet)
        gui.setItem(47, target.inventory.chestplate)
        gui.setItem(48, target.inventory.leggings)
        gui.setItem(49, target.inventory.boots)
        gui.setItem(51, target.inventory.itemInOffHand)
        gui.setItem(52, target.itemOnCursor)

        if (getConfig().getBoolean("invsee.gui.fillEmptySlots", true)) {
            val fillerItem = ItemBuilder(Material.valueOf(getConfig().getString("invsee.gui.fillerMaterial", "BARRIER"))).setHideTooltip(true).build()
            for (i in 36 until 45) {
                gui.setItem(i, fillerItem)
            }
            gui.setItem(45, fillerItem)
            gui.setItem(50, fillerItem)
            gui.setItem(53, fillerItem)
        }

        player.setMetadata("invsee_target", FixedMetadataValue(main, target.uniqueId))
        player.openInventory(gui)

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (!sender.hasPermission("sCore.command.invsee")) return completions

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

    @EventHandler
    fun onInvClick(event: InventoryClickEvent) {
        val player = event.whoClicked
        if (!player.hasMetadata("invsee_target")) return

        event.isCancelled = true
    }

    @EventHandler
    fun onInvClose(event: InventoryCloseEvent) {
        val player = event.player
        if (!player.hasMetadata("invsee_target")) return

        player.removeMetadata("invsee_target", main)
    }

}