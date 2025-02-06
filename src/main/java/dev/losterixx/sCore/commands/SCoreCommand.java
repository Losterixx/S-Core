package dev.losterixx.sCore.commands;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.Main;
import dev.losterixx.sCore.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class SCoreCommand implements CommandExecutor, TabCompleter {

    private MiniMessage mm = Main.mm;
    private Main main = Main.getInstance();
    private ConfigManager configManager = main.getConfigManager();
    private YamlDocument config = configManager.getConfig("config");
    private YamlDocument messages = configManager.getConfig("messages");
    private Component prefix = mm.deserialize(config.getString("prefix"));

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("sCore.admin")) {
            sender.sendMessage(prefix.append(mm.deserialize(messages.getString("general.noPerms"))));
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.score.usage"))));
            return false;
        }

        switch (args[0].toLowerCase()) {

            default -> {
                sender.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.score.usage"))));
            }

            case "about" -> {
                sender.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.score.about").replaceAll("%version%", main.getDescription().getVersion()))));
            }

            case "reload" -> {
                sender.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.score.reload.reloading"))));
                configManager.saveAllConfigs();
                configManager.reloadAllConfigs();
                sender.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.score.reload.reloaded"))));
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
