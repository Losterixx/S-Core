package dev.losterixx.sCore.features.warps

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class DelWarpCommand : CommandExecutor, TabCompleter {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getData() = ConfigManager.getConfig("data")
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPlayer")))
            return false
        }

        if (!sender.hasPermission("sCore.command.delwarp")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.size != 1) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.delwarp.usage")))
            return false
        }

        val warpName = args[0]

        if (!getData().contains("warps.$warpName")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.delwarp.notFound")
                .replace("%warp%", warpName)))
            return false
        }

        getData().remove("warps.$warpName")
        ConfigManager.saveConfig("data")

        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.delwarp.deleted")
            .replace("%warp%", warpName)))

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (!sender.hasPermission("sCore.command.delwarp")) return completions
        if (getData().getSection("warps").getRoutesAsStrings(false).isEmpty()) return completions

        if (args.isEmpty()) {
            for (key in getData().getSection("warps").getRoutesAsStrings(false)) {
                completions.add(key)
            }
        } else if (args.size == 1) {
            for (key in getData().getSection("warps").getRoutesAsStrings(false)) {
                if (key.lowercase().startsWith(args[0].lowercase())) {
                    completions.add(key)
                }
            }
        }

        return completions
    }

}