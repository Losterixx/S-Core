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
import org.bukkit.entity.Player;

public class SetWarpCommand implements CommandExecutor {

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

        if (!sender.hasPermission("sCore.command.setwarp")) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.setwarp.usage"))));
            return false;
        }

        String warpName = args[0];

        if (getData().contains("warps." + warpName)) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.setwarp.alreadExists").replaceAll("%warp%", warpName))));
            return false;
        }

        Player player = (Player) sender;
        Location loc = player.getLocation();

        getData().set("warps." + warpName + ".world", loc.getWorld().getName());
        getData().set("warps." + warpName + ".x", loc.getX());
        getData().set("warps." + warpName + ".y", loc.getY());
        getData().set("warps." + warpName + ".z", loc.getZ());
        getData().set("warps." + warpName + ".yaw", loc.getYaw());
        getData().set("warps." + warpName + ".pitch", loc.getPitch());

        configManager.saveConfig("data");

        player.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.setwarp.set").replaceAll("%warp%", warpName))));

        return false;
    }

}
