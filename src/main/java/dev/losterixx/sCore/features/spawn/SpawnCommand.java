package dev.losterixx.sCore.features.spawn;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.Main;
import dev.losterixx.sCore.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

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

        if (!sender.hasPermission("sCore.command.spawn")) {
            sender.sendMessage(prefix.append(mm.deserialize(messages.getString("general.noPerms"))));
            return false;
        }

        if (args.length != 0) {
            sender.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.spawn.usage"))));
            return false;
        }

        Player player = (Player) sender;
        String world = data.getString("spawn.world");

        if (world == null) {
            player.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.spawn.noSpawnSet"))));
            return false;
        }

        double x = data.getDouble("spawn.x");
        double y = data.getDouble("spawn.y");
        double z = data.getDouble("spawn.z");
        double yaw = data.getDouble("spawn.yaw");
        double pitch = data.getDouble("spawn.pitch");

        player.teleport(new Location(player.getServer().getWorld(world), x, y, z, (float) yaw, (float) pitch));
        player.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.spawn.teleported"))));

        if (messages.getBoolean("commands.spawn.sound.enabled")) {
            Sound sound = Sound.valueOf(messages.getString("commands.spawn.sound.name"));
            double volume = messages.getDouble("commands.spawn.sound.volume");
            double pitchSound = messages.getDouble("commands.spawn.sound.pitch");
            player.playSound(player, sound, (float) volume, (float) pitchSound);
        }

        return false;
    }

}
