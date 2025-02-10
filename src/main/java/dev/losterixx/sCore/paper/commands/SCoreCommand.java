package dev.losterixx.sCore.paper.commands;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.general.GeneralMain;
import dev.losterixx.sCore.paper.PaperMain;
import dev.losterixx.sCore.paper.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class SCoreCommand implements CommandExecutor, TabCompleter {

    private MiniMessage mm = PaperMain.mm;
    private PaperMain main = PaperMain.getInstance();
    private ConfigManager configManager = main.getConfigManager();
    private YamlDocument getConfig() { return configManager.getConfig("config"); }
    private YamlDocument getMessages() { return configManager.getConfig("messages"); }
    private Component getPrefix() { return mm.deserialize(getConfig().getString("prefix")); }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("sCore.admin")) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.score.usage"))));
            return false;
        }

        switch (args[0].toLowerCase()) {

            default -> {
                sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.score.usage"))));
            }

            case "about" -> {
                sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.score.about").replaceAll("%version%", GeneralMain.PLUGIN_VERSION))));
            }

            case "reload" -> {
                sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.score.reload.reloading"))));
                long now = System.currentTimeMillis();
                configManager.reloadAllConfigs();
                long timeElapsedMillis = System.currentTimeMillis() - now;
                double timeElapsedSeconds = timeElapsedMillis / 1000.0;
                String formattedTimeElapsed = String.format("%.3f", timeElapsedSeconds).replaceAll(",", ".");
                sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.score.reload.reloaded").replaceAll("%time%", String.valueOf(formattedTimeElapsed)))));
            }

        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 0) {
            completions.add("about");
            completions.add("reload");
        } else if (args.length == 1) {
            if ("about".startsWith(args[0].toLowerCase())) completions.add("about");
            if ("reload".startsWith(args[0].toLowerCase())) completions.add("reload");
        }

        return completions;
    }

}
