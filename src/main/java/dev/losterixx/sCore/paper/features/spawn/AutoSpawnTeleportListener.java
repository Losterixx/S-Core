package dev.losterixx.sCore.paper.features.spawn;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.paper.PaperMain;
import dev.losterixx.sCore.paper.utils.ConfigManager;
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

    private MiniMessage mm = PaperMain.mm;
    private PaperMain main = PaperMain.getInstance();
    private ConfigManager configManager = main.getConfigManager();
    private YamlDocument getConfig() { return configManager.getConfig("config"); }
    private YamlDocument getData() { return configManager.getConfig("data"); }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            if (getConfig().getBoolean("autoSpawnTeleport.onFirstJoin")) {
                teleportToSpawn(player);
            }
        } else {
            if (getConfig().getBoolean("autoSpawnTeleport.onJoin")) {
                teleportToSpawn(player);
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (getConfig().getBoolean("autoSpawnTeleport.onRespawn")) {
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

        if (getConfig().getBoolean("autoSpawnTeleport.onHeight.enabled")) {
            int height = getConfig().getInt("autoSpawnTeleport.onHeight.height");
            if (player.getLocation().getY() < height) {
                teleportToSpawn(player);
            }
        }
    }

    private void teleportToSpawn(Player player) {
        if (!getData().contains("spawn")) {
            main.getLogger().warning("Spawn location is not set!");
            return;
        }

        String world = getData().getString("spawn.world");
        double x = getData().getDouble("spawn.x");
        double y = getData().getDouble("spawn.y");
        double z = getData().getDouble("spawn.z");
        double yaw = getData().getDouble("spawn.yaw");
        double pitch = getData().getDouble("spawn.pitch");

        player.teleport(new Location(player.getServer().getWorld(world), x, y, z, (float) yaw, (float) pitch));
    }

}
