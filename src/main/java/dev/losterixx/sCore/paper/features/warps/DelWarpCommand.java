package dev.losterixx.sCore.paper.features.warps;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.paper.PaperMain;
import dev.losterixx.sCore.paper.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DelWarpCommand implements CommandExecutor, TabCompleter {

    private MiniMessage mm = PaperMain.mm;
    private PaperMain main = PaperMain.getInstance();
    private ConfigManager configManager = main.getConfigManager();
    private YamlDocument getConfig() { return configManager.getConfig("config"); }
    private YamlDocument getMessages() { return configManager.getConfig("messages"); }
    private YamlDocument getData() { return configManager.getConfig("data"); }
    private Component getPrefix() { return mm.deserialize(getConfig().getString("prefix")); }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPlayer"))));
            return false;
        }

        if (!sender.hasPermission("sCore.command.delwarp")) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.delwarp.usage"))));
            return false;
        }

        String warpName = args[0];

        if (!getData().contains("warps." + warpName)) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.delwarp.notFound").replaceAll("%warp%", warpName))));
            return false;
        }

        Player player = (Player) sender;

        getData().remove("warps." + warpName);
        configManager.saveConfig("data");

        player.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.delwarp.deleted").replaceAll("%warp%", warpName))));

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("sCore.commands.delwarp")) return completions;

        if (args.length == 0) {
            List<String> keys = new ArrayList<>();
            for (Object key : getData().getSection("warps").getKeys()) {
                keys.add((String) key);
            }

            for (String key : keys) {
                completions.add(key);
            }
        } else if (args.length == 1) {
            List<String> keys = new ArrayList<>();
            for (Object key : getData().getSection("warps").getKeys()) {
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
