package dev.losterixx.sCore.features.autobroadcaster;

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

public class BroadcastCommand implements CommandExecutor, TabCompleter {

    private MiniMessage mm = Main.mm;
    private Main main = Main.getInstance();
    private ConfigManager configManager = main.getConfigManager();
    private YamlDocument getConfig() { return configManager.getConfig("config"); }
    private YamlDocument getMessages() { return configManager.getConfig("messages"); }
    private Component getPrefix() { return mm.deserialize(getConfig().getString("prefix")); }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("sCore.commands.broadcast")) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.broadcast.usage"))));
            return false;
        }

        String messageId = args[0].toLowerCase();

        if (!getConfig().contains("autoBroadcaster.messages." + messageId)) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.broadcast.notFound").replaceAll("%messageId%", messageId))));
            return false;
        }

        BroadcastManager.broadcast(messageId);
        sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.broadcast.sent").replaceAll("%messageId%", messageId))));

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("sCore.commands.broadcast")) return completions;

        if (args.length == 0) {
            List<String> keys = new ArrayList<>();
            for (Object key : getConfig().getSection("autoBroadcaster.messages").getKeys()) {
                keys.add((String) key);
            }

            for (String key : keys) {
                completions.add(key);
            }
        } else if (args.length == 1) {
            List<String> keys = new ArrayList<>();
            for (Object key : getConfig().getSection("autoBroadcaster.messages").getKeys()) {
                keys.add((String) key);
            }

            for (String key : keys) {
                if (key.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(key);
                }
            }
        }

        return completions;
    }

}
