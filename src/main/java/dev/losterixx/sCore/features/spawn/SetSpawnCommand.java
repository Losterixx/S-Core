package dev.losterixx.sCore.features.spawn;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.Main;
import dev.losterixx.sCore.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

    private MiniMessage mm = Main.mm;
    private Main main = Main.getInstance();
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

        if (!sender.hasPermission("sCore.command.setspawn")) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return false;
        }

        if (args.length != 0) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.setspawn.usage"))));
            return false;
        }

        Player player = (Player) sender;
        Location loc = player.getLocation();

        getData().set("spawn.world", loc.getWorld().getName());
        getData().set("spawn.x", loc.getX());
        getData().set("spawn.y", loc.getY());
        getData().set("spawn.z", loc.getZ());
        getData().set("spawn.yaw", loc.getYaw());
        getData().set("spawn.pitch", loc.getPitch());

        configManager.saveConfig("data");

        player.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.setspawn.set"))));

        return false;
    }

}
