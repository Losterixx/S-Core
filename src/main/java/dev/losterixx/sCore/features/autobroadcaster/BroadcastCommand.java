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

    private static MiniMessage mm = Main.mm;
    private static Main main = Main.getInstance();
    private static ConfigManager configManager = main.getConfigManager();
    private static YamlDocument config = configManager.getConfig("config");
    private static YamlDocument messages = configManager.getConfig("messages");
    private static Component prefix = mm.deserialize(config.getString("prefix"));

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("sCore.commands.broadcast")) {
            sender.sendMessage(prefix.append(mm.deserialize(messages.getString("general.noPerms"))));
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.broadcast.usage"))));
            return false;
        }

        String messageId = args[0].toLowerCase();

        if (!config.contains("autoBroadcaster.messages." + messageId)) {
            sender.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.broadcast.notFound").replaceAll("%messageId%", messageId))));
            return false;
        }

        BroadcastManager.broadcast(messageId);
        sender.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.broadcast.sent").replaceAll("%messageId%", messageId))));

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("sCore.commands.broadcast")) return completions;

        if (args.length == 0) {
            List<String> keys = new ArrayList<>();
            for (Object key : config.getSection("autoBroadcaster.messages").getKeys()) {
                keys.add((String) key);
            }

            for (String key : keys) {
                completions.add(key);
            }
        } else if (args.length == 1) {
            List<String> keys = new ArrayList<>();
            for (Object key : config.getSection("autoBroadcaster.messages").getKeys()) {
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

    public static void updateConfigs() {
        config = configManager.getConfig("config");
        messages = configManager.getConfig("messages");
        prefix = mm.deserialize(config.getString("prefix"));
    }

}
