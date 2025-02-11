package dev.losterixx.sCore.paper.features.gamemode;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.general.GeneralMain;
import dev.losterixx.sCore.paper.PaperMain;
import dev.losterixx.sCore.paper.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GamemodeCommand implements CommandExecutor, TabCompleter {

    private MiniMessage mm = PaperMain.mm;
    private PaperMain main = PaperMain.getInstance();
    private ConfigManager configManager = main.getConfigManager();
    private YamlDocument getConfig() { return configManager.getConfig("config"); }
    private YamlDocument getMessages() { return configManager.getConfig("messages"); }
    private Component getPrefix() { return mm.deserialize(getConfig().getString("prefix")); }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPlayer"))));
            return false;
        }

        if (!sender.hasPermission("sCore.command.gamemode.survival.self") && !sender.hasPermission("sCore.command.gamemode.creative.self") && !sender.hasPermission("sCore.command.gamemode.adventure.self") && !sender.hasPermission("sCore.command.gamemode.spectator.self")) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return false;
        } else if (!sender.hasPermission("sCore.command.gamemode.survival.other") && !sender.hasPermission("sCore.command.gamemode.creative.other") && !sender.hasPermission("sCore.command.gamemode.adventure.other") && !sender.hasPermission("sCore.command.gamemode.spectator.other")) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return false;
        }

        if (args.length == 0 || args.length > 2) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.usage"))));
            return false;
        }

        switch (args[0].toLowerCase()) {

            default -> {
                sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.usage"))));
            }

            case "survival", "s", "0" -> {
                if (args.length == 1) {
                    if (!sender.hasPermission("sCore.command.gamemode.survival.self")) {
                        sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
                        return false;
                    }

                    ((Player) sender).setGameMode(GameMode.SURVIVAL);
                    sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.changed-self").replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.survival")))));
                    return true;
                } else if (args.length == 2) {
                    if (!sender.hasPermission("sCore.command.gamemode.survival.other")) {
                        sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
                        return false;
                    }

                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.playerNotFound"))));
                        return false;
                    }

                    target.setGameMode(GameMode.SURVIVAL);
                    sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.changed-other").replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.survival")).replace("%player%", target.getName()))));
                    sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.changed-self").replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.survival")))));
                    return true;
                } else {
                    sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.usage"))));
                    return false;
                }
            }

            case "creative", "c", "1" -> {
                if (args.length == 1) {
                    if (!sender.hasPermission("sCore.command.gamemode.creative.self")) {
                        sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
                        return false;
                    }

                    ((Player) sender).setGameMode(GameMode.CREATIVE);
                    sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.changed-self").replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.creative")))));
                    return true;
                } else if (args.length == 2) {
                    if (!sender.hasPermission("sCore.command.gamemode.creative.other")) {
                        sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
                        return false;
                    }

                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.playerNotFound"))));
                        return false;
                    }

                    target.setGameMode(GameMode.CREATIVE);
                    sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.changed-other").replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.creative")).replace("%player%", target.getName()))));
                    sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.changed-self").replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.creative")))));
                    return true;
                } else {
                    sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.usage"))));
                    return false;
                }
            }

            case "adventure", "a", "2" -> {
                if (args.length == 1) {
                    if (!sender.hasPermission("sCore.command.gamemode.adventure.self")) {
                        sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
                        return false;
                    }

                    ((Player) sender).setGameMode(GameMode.ADVENTURE);
                    sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.changed-self").replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.adventure")))));
                    return true;
                } else if (args.length == 2) {
                    if (!sender.hasPermission("sCore.command.gamemode.adventure.other")) {
                        sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
                        return false;
                    }

                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.playerNotFound"))));
                        return false;
                    }

                    target.setGameMode(GameMode.ADVENTURE);
                    sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.changed-other").replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.adventure")).replace("%player%", target.getName()))));
                    sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.changed-self").replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.adventure")))));
                    return true;
                } else {
                    sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.usage"))));
                    return false;
                }
            }

            case "spectator", "sp", "spec", "3" -> {
                if (args.length == 1) {
                    if (!sender.hasPermission("sCore.command.gamemode.spectator.self")) {
                        sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
                        return false;
                    }

                    ((Player) sender).setGameMode(GameMode.SPECTATOR);
                    sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.changed-self").replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.spectator")))));
                    return true;
                } else if (args.length == 2) {
                    if (!sender.hasPermission("sCore.command.gamemode.spectator.other")) {
                        sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
                        return false;
                    }

                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.playerNotFound"))));
                        return false;
                    }

                    target.setGameMode(GameMode.SPECTATOR);
                    sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.changed-other").replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.spectator")).replace("%player%", target.getName()))));
                    sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.changed-self").replace("%gamemode%", getMessages().getString("commands.gamemode.gamemodes.spectator")))));
                    return true;
                } else {
                    sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.gamemode.usage"))));
                    return false;
                }
            }

        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 0) {
            if (sender.hasPermission("sCore.command.gamemode.survival.self") || sender.hasPermission("sCore.command.gamemode.survival.other")) completions.add("survival");
            if (sender.hasPermission("sCore.command.gamemode.creative.self") || sender.hasPermission("sCore.command.gamemode.creative.other")) completions.add("creative");
            if (sender.hasPermission("sCore.command.gamemode.adventure.self") || sender.hasPermission("sCore.command.gamemode.adventure.other")) completions.add("adventure");
            if (sender.hasPermission("sCore.command.gamemode.spectator.self") || sender.hasPermission("sCore.command.gamemode.spectator.other")) completions.add("spectator");
        } else if (args.length == 1) {
            if ("survival".startsWith(args[0].toLowerCase()) && (sender.hasPermission("sCore.command.gamemode.survival.self") || sender.hasPermission("sCore.command.gamemode.survival.other"))) completions.add("survival");
            if ("creative".startsWith(args[0].toLowerCase()) && (sender.hasPermission("sCore.command.gamemode.creative.self") || sender.hasPermission("sCore.command.gamemode.creative.other"))) completions.add("creative");
            if ("adventure".startsWith(args[0].toLowerCase()) && (sender.hasPermission("sCore.command.gamemode.adventure.self") || sender.hasPermission("sCore.command.gamemode.adventure.other"))) completions.add("adventure");
            if ("spectator".startsWith(args[0].toLowerCase()) && (sender.hasPermission("sCore.command.gamemode.spectator.self") || sender.hasPermission("sCore.command.gamemode.spectator.other"))) completions.add("spectator");
        } else if (args.length == 2) {
            if (!sender.hasPermission("sCore.command.gamemode.survival.other") && !sender.hasPermission("sCore.command.gamemode.creative.other") && !sender.hasPermission("sCore.command.gamemode.adventure.other") && !sender.hasPermission("sCore.command.gamemode.spectator.other")) return completions;

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) completions.add(player.getName());
            }
        }

        return completions;
    }

}
