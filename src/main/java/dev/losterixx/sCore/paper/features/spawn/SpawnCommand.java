package dev.losterixx.sCore.paper.features.spawn;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.paper.PaperMain;
import dev.losterixx.sCore.paper.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

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

        if (!sender.hasPermission("sCore.command.spawn")) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return false;
        }

        if (args.length != 0) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.spawn.usage"))));
            return false;
        }

        Player player = (Player) sender;
        String world = getData().getString("spawn.world");

        if (world == null) {
            player.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.spawn.noSpawnSet"))));
            return false;
        }

        double x = getData().getDouble("spawn.x");
        double y = getData().getDouble("spawn.y");
        double z = getData().getDouble("spawn.z");
        double yaw = getData().getDouble("spawn.yaw");
        double pitch = getData().getDouble("spawn.pitch");

        player.teleport(new Location(player.getServer().getWorld(world), x, y, z, (float) yaw, (float) pitch));
        player.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.spawn.teleported"))));

        if (getMessages().getBoolean("commands.spawn.sound.enabled")) {
            Sound sound = Sound.valueOf(getMessages().getString("commands.spawn.sound.name"));
            double volume = getMessages().getDouble("commands.spawn.sound.volume");
            double pitchSound = getMessages().getDouble("commands.spawn.sound.pitch");
            player.playSound(player, sound, (float) volume, (float) pitchSound);
        }

        return false;
    }

}
