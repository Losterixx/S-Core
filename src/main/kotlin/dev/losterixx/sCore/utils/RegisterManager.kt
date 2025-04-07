package dev.losterixx.sCore.utils

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.commands.SCoreCommand
import dev.losterixx.sCore.features.autobroadcaster.BroadcastCommand
import dev.losterixx.sCore.features.custom.customactions.CustomActionsListener
import dev.losterixx.sCore.features.custommessages.CustomMessagesListener
import dev.losterixx.sCore.features.gamemode.AutoGamemodeListener
import dev.losterixx.sCore.features.gamemode.GamemodeCommand
import dev.losterixx.sCore.features.infocommands.*
import dev.losterixx.sCore.features.invsee.InvseeCommand
import dev.losterixx.sCore.features.msg.MsgCommand
import dev.losterixx.sCore.features.msg.ReplyCommand
import dev.losterixx.sCore.features.spawn.AutoSpawnTpListener
import dev.losterixx.sCore.features.spawn.SetSpawnCommand
import dev.losterixx.sCore.features.spawn.SpawnCommand
import dev.losterixx.sCore.features.warps.DelWarpCommand
import dev.losterixx.sCore.features.warps.ListWarpsCommand
import dev.losterixx.sCore.features.warps.SetWarpCommand
import dev.losterixx.sCore.features.warps.WarpCommand
import dev.losterixx.sCore.features.whois.WhoisCommand
import dev.losterixx.sCore.other.updatechecker.UpdateListener
import org.bukkit.Bukkit
import org.bukkit.command.*
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

object RegisterManager {

    private val main = Main.instance
    private fun getModules() = ConfigManager.getConfig("modules")

    private var commands = 0
    private var listeners = 0

    private fun registerCommands() {
        registerCommand("sCore", SCoreCommand(), SCoreCommand(), "score", "s-core")
        if (getModules().getBoolean("spawn")) registerCommand("setspawn", SetSpawnCommand(), null)
        if (getModules().getBoolean("spawn")) registerCommand("spawn", SpawnCommand(), null)
        if (getModules().getBoolean("gamemode")) registerCommand("gamemode", GamemodeCommand(), GamemodeCommand(), "gm")
        if (getModules().getBoolean("auto-broadcaster")) registerCommand("broadcast", BroadcastCommand(), BroadcastCommand(), "bc")
        if (getModules().getBoolean("info-commands")) registerCommand("discord", DiscordCommand(), null, "dc")
        if (getModules().getBoolean("info-commands")) registerCommand("website", WebsiteCommand(), null, "web")
        if (getModules().getBoolean("info-commands")) registerCommand("vote", VoteCommand(), null)
        if (getModules().getBoolean("info-commands")) registerCommand("help", HelpCommand(), null)
        if (getModules().getBoolean("info-commands")) registerCommand("rules", RulesCommand(), null)
        if (getModules().getBoolean("msg")) registerCommand("msg", MsgCommand(), MsgCommand(), "w", "whisper", "tell", "t")
        if (getModules().getBoolean("msg")) registerCommand("reply", ReplyCommand(), null, "r")
        if (getModules().getBoolean("invsee")) registerCommand("invsee", InvseeCommand(), InvseeCommand(), "invs")
        if (getModules().getBoolean("whois")) registerCommand("whois", WhoisCommand(), WhoisCommand(), "playerinfo")
        if (getModules().getBoolean("warps")) registerCommand("setwarp", SetWarpCommand(), null)
        if (getModules().getBoolean("warps")) registerCommand("warp", WarpCommand(), WarpCommand())
        if (getModules().getBoolean("warps")) registerCommand("delwarp", DelWarpCommand(), DelWarpCommand())
        if (getModules().getBoolean("warps")) registerCommand("listwarps", ListWarpsCommand(), null, "warps")

        main.logger.info("Registered $commands commands!")
    }

    private fun registerListeners() {
        HandlerList.unregisterAll(main)

        registerListener(UpdateListener())
        if (getModules().getBoolean("spawn")) registerListener(AutoSpawnTpListener())
        if (getModules().getBoolean("gamemode")) registerListener(AutoGamemodeListener())
        if (getModules().getBoolean("custom-messages")) registerListener(CustomMessagesListener())
        if (getModules().getBoolean("msg")) registerListener(ReplyCommand())
        if (getModules().getBoolean("invsee")) registerListener(InvseeCommand())
        if (getModules().getBoolean("customActions")) registerListener(CustomActionsListener())

        main.logger.info("Registered $listeners listeners!")
    }

    fun registerAll() {
        registerCommands()
        registerListeners()
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
            commands++
        } catch (e: Exception) {
            main.logger.warning("Failed to register command $name!")
            e.printStackTrace()
        }
    }

    private fun registerListener(listener: Listener) {
        try {
            main.server.pluginManager.registerEvents(listener, main)
            listeners++
        } catch (e: Exception) {
            main.logger.warning("Failed to register event ${listener.javaClass.simpleName}!")
            e.printStackTrace()
        }
    }

}