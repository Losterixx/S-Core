package dev.losterixx.sCore.features.autobroadcaster;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.Main;
import dev.losterixx.sCore.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BroadcastManager {

    private static MiniMessage mm = Main.mm;
    private static Main main = Main.getInstance();
    private static ConfigManager configManager = main.getConfigManager();
    private static YamlDocument config = configManager.getConfig("config");
    private static Component prefix = mm.deserialize(config.getString("prefix"));

    public static void broadcast(String messageId) {
        if (!config.contains("autoBroadcaster.messages." + messageId)) {
            main.getLogger().warning("Message with ID " + messageId + " could not be found!");
            return;
        }

        List<String> messagesToSent = config.getStringList("autoBroadcaster.messages." + messageId);

        if (messagesToSent.isEmpty()) {
            main.getLogger().warning("Message with ID " + messageId + " is empty!");
            return;
        }

        for (String message : messagesToSent) {
            if (message.replaceAll(" ", "").isEmpty()) {
                message = "<gray> ";
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(prefix.append(mm.deserialize(message)));
            }

            Bukkit.getConsoleSender().sendMessage(prefix.append(mm.deserialize(message)));
        }

        if (config.getBoolean("autoBroadcaster.sound.enabled")) {
            Sound sound = Sound.valueOf(config.getString("autoBroadcaster.sound.name"));
            double volume = config.getDouble("autoBroadcaster.sound.volume");
            double pitch = config.getDouble("autoBroadcaster.sound.pitch");

            main.getServer().getOnlinePlayers().forEach(player -> {
                player.playSound(player, sound, (float) volume, (float) pitch);
            });
        }
    }

    public void startBroadcasting() {
        int interval = config.getInt("autoBroadcaster.interval");

        if (interval <= 0)
            return;

        short type = config.getShort("autoBroadcaster.type");

        if (!config.getBoolean("autoBroadcaster.enabled"))
            return;

        new BukkitRunnable() {
            private int index = 0;

            @Override
            public void run() {
                if (type == 1) { // SEQUENTIAL
                    List<String> keys = new ArrayList<>();
                    for (Object key : config.getSection("autoBroadcaster.messages").getKeys()) {
                        keys.add((String) key);
                    }
                    if (keys.isEmpty()) return;

                    String messageId = keys.get(index);
                    broadcast(messageId);

                    index = (index + 1) % keys.size();
                } else if (type == 2) { // RANDOM
                    List<String> keys = new ArrayList<>();
                    for (Object key : config.getSection("autoBroadcaster.messages").getKeys()) {
                        keys.add((String) key);
                    }
                    if (keys.isEmpty()) return;

                    String messageId = keys.get(new Random().nextInt(keys.size()));
                    broadcast(messageId);
                }
            }
        }.runTaskTimer(main, (interval * 20L) / 2, interval * 20L); // interval in seconds
    }

    public static void updateConfigs() {
        config = configManager.getConfig("config");
        prefix = mm.deserialize(config.getString("prefix"));
    }

}
