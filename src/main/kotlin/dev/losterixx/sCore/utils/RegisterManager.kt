package dev.losterixx.sCore.utils

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.commands.SCoreCommand
import dev.losterixx.sCore.features.gamemode.AutoGamemodeListener
import dev.losterixx.sCore.features.gamemode.GamemodeCommand
import dev.losterixx.sCore.features.spawn.AutoSpawnTpListener
import dev.losterixx.sCore.features.spawn.SetSpawnCommand
import dev.losterixx.sCore.features.spawn.SpawnCommand
import org.bukkit.Bukkit
import org.bukkit.command.*
import org.bukkit.event.HandlerList

class RegisterManager(private val main: Main) {

    private fun registerCommands() {
        registerCommand("sCore", SCoreCommand(), SCoreCommand(), "score", "s-core")
        registerCommand("setspawn", SetSpawnCommand(), null)
        registerCommand("spawn", SpawnCommand(), null)
        registerCommand("gamemode", GamemodeCommand(), GamemodeCommand(), "gm")
    }

    private fun registerEvents() {
        HandlerList.unregisterAll(main)

        main.server.pluginManager.registerEvents(AutoSpawnTpListener(), main)
        main.server.pluginManager.registerEvents(AutoGamemodeListener(), main)
    }

    fun register() {
        registerCommands()
        registerEvents()
    }


    private fun registerCommand(
        name: String,
        executor: CommandExecutor,
        completer: TabCompleter?,
        vararg aliases: String?
    ) {
        try {
            val commandMapField = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
            commandMapField.isAccessible = true
            val commandMap = commandMapField[Bukkit.getServer()] as CommandMap

            val command: Command = object : Command(name) {
                override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
                    return executor.onCommand(sender, this, commandLabel, args)
                }

                override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): List<String> {
                    if (completer == null) return super.tabComplete(sender, alias, args)
                    return completer.onTabComplete(sender, this, alias, args)!!
                }
            }

            if (aliases != null && aliases.size > 0 && aliases[0] != null && !aliases[0]!!.isEmpty()) {
                command.setAliases(listOf(*aliases))
            }

            commandMap.register(name, command)
        } catch (e: Exception) {
            main.logger.warning("Failed to register command $name!")
            e.printStackTrace()
        }
    }

}