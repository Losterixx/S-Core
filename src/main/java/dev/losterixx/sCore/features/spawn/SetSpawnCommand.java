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
    private YamlDocument config = configManager.getConfig("config");
    private YamlDocument messages = configManager.getConfig("messages");
    private YamlDocument data = configManager.getConfig("data");
    private Component prefix = mm.deserialize(config.getString("prefix"));

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix.append(mm.deserialize(messages.getString("general.noPlayer"))));
            return false;
        }

        if (!sender.hasPermission("sCore.command.setspawn")) {
            sender.sendMessage(prefix.append(mm.deserialize(messages.getString("general.noPerms"))));
            return false;
        }

        if (args.length != 0) {
            sender.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.setspawn.usage"))));
            return false;
        }

        Player player = (Player) sender;
        Location loc = player.getLocation();

        data.set("spawn.world", loc.getWorld().getName());
        data.set("spawn.x", loc.getX());
        data.set("spawn.y", loc.getY());
        data.set("spawn.z", loc.getZ());
        data.set("spawn.yaw", loc.getYaw());
        data.set("spawn.pitch", loc.getPitch());

        configManager.saveConfig("data");

        player.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.setspawn.set"))));

        return false;
    }

}
