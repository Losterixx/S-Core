package dev.losterixx.sCore.features.msg

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player


class MsgCommand : CommandExecutor, TabCompleter {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    companion object {
        fun getAllowedCharactersRegex(): String = ConfigManager.getConfig("config").getString("msg.allowedCharactersRegex", "^[A-Za-z0-9ÄÖÜäöüß_\\-+&/\\[\\](){}\\\\?!.;,:´`^°#'|<>\"$€@%=*~\\\\ ]+$")
        val lastMessages: MutableMap<Player?, Player?> = HashMap<Player?, Player?>()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.notPlayer")))
            return false
        }

        if (!sender.hasPermission("sCore.command.msg")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.size < 2) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.msg.usage")))
            return false
        }

        val player = sender as Player
        val target = Bukkit.getPlayer(args[0])

        if (target == null || !target.isOnline) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.playerNotFound")))
            return false
        }

        if (target.uniqueId == player.uniqueId) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.msg.selfError")))
            return false
        }

        val message = args.copyOfRange(1, args.size).joinToString(" ")

        if (!message.matches(getAllowedCharactersRegex().toRegex())) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.msg.invalidChars")))
            return false
        }

        val textColor = getConfig().getString("msg.textColor", "#FFFFFF")

        val targetSelfMessage = getConfig().getString("msg.target-self")?.replace("%player%", player.name)!!.replace("%target%", target.name)
        val selfTargetMessage = getConfig().getString("msg.self-target")?.replace("%player%", player.name)!!.replace("%target%", target.name)

        val formattedMessageToTarget = mm.deserialize(targetSelfMessage).append(Component.text(message).color(TextColor.fromHexString(textColor)))
        val formattedMessageToSender = mm.deserialize(selfTargetMessage).append(Component.text(message).color(TextColor.fromHexString(textColor)))

        player.sendMessage(formattedMessageToTarget)
        target.sendMessage(formattedMessageToSender)

        lastMessages[player] = target


        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (!sender.hasPermission("sCore.command.msg")) return completions

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