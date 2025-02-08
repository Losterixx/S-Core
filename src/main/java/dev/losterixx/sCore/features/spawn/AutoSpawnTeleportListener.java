package dev.losterixx.sCore.features.spawn;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.Main;
import dev.losterixx.sCore.utils.ConfigManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSpawnTeleportListener implements Listener {

    private MiniMessage mm = Main.mm;
    private Main main = Main.getInstance();
    private ConfigManager configManager = main.getConfigManager();
    private YamlDocument config = configManager.getConfig("config");
    private YamlDocument data = configManager.getConfig("data");

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            if (config.getBoolean("autoSpawnTeleport.onFirstJoin")) {
                teleportToSpawn(player);
            }
        } else {
            if (config.getBoolean("autoSpawnTeleport.onJoin")) {
                teleportToSpawn(player);
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (config.getBoolean("autoSpawnTeleport.onRespawn")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    teleportToSpawn(player);
                }
            }.runTaskLater(main, 3);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (config.getBoolean("autoSpawnTeleport.onHeight.enabled")) {
            int height = config.getInt("autoSpawnTeleport.onHeight.height");
            if (player.getLocation().getY() < height) {
                teleportToSpawn(player);
            }
        }
    }

    private void teleportToSpawn(Player player) {
        String world = data.getString("spawn.world");
        double x = data.getDouble("spawn.x");
        double y = data.getDouble("spawn.y");
        double z = data.getDouble("spawn.z");
        double yaw = data.getDouble("spawn.yaw");
        double pitch = data.getDouble("spawn.pitch");

        player.teleport(new Location(player.getServer().getWorld(world), x, y, z, (float) yaw, (float) pitch));
    }

}
