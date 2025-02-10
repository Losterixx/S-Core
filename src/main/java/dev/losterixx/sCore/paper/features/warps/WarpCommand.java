package dev.losterixx.sCore.paper.features.warps;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.paper.PaperMain;
import dev.losterixx.sCore.paper.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpCommand implements CommandExecutor, TabCompleter {

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

        if (!sender.hasPermission("sCore.command.warp")) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.warp.usage"))));
            return false;
        }

        String warpName = args[0];

        if (!getData().contains("warps." + warpName)) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.warp.notFound").replaceAll("%warp%", warpName))));
            return false;
        }

        Player player = (Player) sender;

        World world = Bukkit.getWorld(getData().getString("warps." + warpName + ".world"));

        if (world == null) {
            player.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.warp.locNotFound").replaceAll("%warp%", warpName))));
            return false;
        }

        double x = getData().getDouble("warps." + warpName + ".x");
        double y = getData().getDouble("warps." + warpName + ".y");
        double z = getData().getDouble("warps." + warpName + ".z");
        double yaw = getData().getDouble("warps." + warpName + ".yaw");
        double pitch = getData().getDouble("warps." + warpName + ".pitch");

        Location loc = new Location(world, x, y, z, (float) yaw, (float) pitch);
        player.teleport(loc);

        player.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.warp.teleported").replaceAll("%warp%", warpName))));

        if (getMessages().getBoolean("commands.warp.sound.enabled")) {
            Sound sound = Sound.valueOf(getMessages().getString("commands.warp.sound.name"));
            double volume = getMessages().getDouble("commands.warp.sound.volume");
            double pitchSound = getMessages().getDouble("commands.warp.sound.pitch");
            player.playSound(player, sound, (float) volume, (float) pitchSound);
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("sCore.commands.warp")) return completions;

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
