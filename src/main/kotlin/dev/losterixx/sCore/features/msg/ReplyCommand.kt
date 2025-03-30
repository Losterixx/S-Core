package dev.losterixx.sCore.features.msg

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class ReplyCommand : CommandExecutor, Listener {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    private fun getAllowedCharactersRegex(): String = getConfig().getString("msg.allowedCharactersRegex", "^[A-Za-z0-9ÄÖÜäöüß_\\-+&/\\[\\](){}\\\\?!.;,:´`^°#'|<>\"$€@%=*~\\\\ ]+$")
    private val lastMessages: MutableMap<Player?, Player?> = HashMap<Player?, Player?>()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.notPlayer")))
            return false
        }

        if (!sender.hasPermission("sCore.command.reply")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.isEmpty()) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.reply.usage")))
            return false
        }

        val player = sender as Player
        val target = MsgCommand.lastMessages[player]

        if (target == null || !target.isOnline) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.reply.noTarget")))
            return false
        }

        val message = args.joinToString(" ")

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

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        MsgCommand.lastMessages.remove(event.getPlayer())
    }

}