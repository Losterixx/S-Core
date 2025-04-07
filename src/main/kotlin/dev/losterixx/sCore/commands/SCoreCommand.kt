package dev.losterixx.sCore.commands

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.Main.Companion.miniMessage
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import java.net.URI
import kotlin.system.measureTimeMillis

class SCoreCommand : CommandExecutor, TabCompleter {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission("sCore.admin")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.isEmpty()) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.s-core.usage")))
            return false
        }

        when (args[0].lowercase()) {

            "about" -> {
                var aboutMessage = getMessages().getString("commands.s-core.about", null)

                if (aboutMessage == null || !aboutMessage.contains("%version%") || !aboutMessage.contains("%author%")) {
                    aboutMessage = "<gray>S-Core v%version% <dark_gray>- <gray>%author%"
                }

                sender.sendMessage(mm.deserialize(getPrefix() + aboutMessage
                    .replace("%version%", main.description.version)
                    .replace("%author%", main.description.authors.joinToString(", "))))
            }

            "reload", "rl" -> {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.s-core.reload.reloading")))

                val elapsedTime = runCatching {
                    measureTimeMillis {
                        ConfigManager.reloadConfig("config")
                        main.loadLangFiles()
                        ConfigManager.reloadAllConfigs()

                        main.server.serverLinks.links.forEach {
                            main.server.serverLinks.removeLink(it)
                        }
                        main.registerServerLinks()
                    }
                }.getOrElse {
                    sender.sendMessage(mm.deserialize(getPrefix() + "<red>Error while reloading configs! Check console."))
                    it.printStackTrace()
                    -1
                }

                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.s-core.reload.reloaded")
                    .replace("%time%", elapsedTime.toString())))
            }

            "help" -> {
                for (line in getMessages().getStringList("commands.s-core.help")) {
                    sender.sendMessage(mm.deserialize(line
                        .replace("%prefix%", getPrefix())))
                }
            }

            else -> {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.s-core.usage")))
            }
        }

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (!sender.hasPermission("sCore.admin")) return completions

        if (args.isEmpty()) {
            completions.add("about")
            completions.add("reload")
            completions.add("help")
        } else if (args.size == 1) {
            if ("about".startsWith(args[0].lowercase())) completions.add("about")
            if ("reload".startsWith(args[0].lowercase())) completions.add("reload")
            if ("help".startsWith(args[0].lowercase())) completions.add("help")
        }

        return completions
    }

}