package dev.losterixx.sCore.paper.features.autobroadcaster;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.paper.PaperMain;
import dev.losterixx.sCore.paper.utils.ConfigManager;
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

    private static MiniMessage mm = PaperMain.mm;
    private static PaperMain main = PaperMain.getInstance();
    private static ConfigManager configManager = main.getConfigManager();
    private static YamlDocument getConfig() { return configManager.getConfig("config"); }
    private static Component getPrefix() { return mm.deserialize(getConfig().getString("prefix")); }

    public static void broadcast(String messageId) {
        if (!getConfig().contains("autoBroadcaster.messages." + messageId)) {
            main.getLogger().warning("Message with ID " + messageId + " could not be found!");
            return;
        }

        List<String> messagesToSent = getConfig().getStringList("autoBroadcaster.messages." + messageId);

        if (messagesToSent.isEmpty()) {
            main.getLogger().warning("Message with ID " + messageId + " is empty!");
            return;
        }

        for (String message : messagesToSent) {
            if (message.replaceAll(" ", "").isEmpty()) {
                message = "<gray> ";
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(getPrefix().append(mm.deserialize(message)));
            }

            Bukkit.getConsoleSender().sendMessage(getPrefix().append(mm.deserialize(message)));
        }

        if (getConfig().getBoolean("autoBroadcaster.sound.enabled")) {
            Sound sound = Sound.valueOf(getConfig().getString("autoBroadcaster.sound.name"));
            double volume = getConfig().getDouble("autoBroadcaster.sound.volume");
            double pitch = getConfig().getDouble("autoBroadcaster.sound.pitch");

            main.getServer().getOnlinePlayers().forEach(player -> {
                player.playSound(player, sound, (float) volume, (float) pitch);
            });
        }
    }

    public void startBroadcasting() {
        int interval = getConfig().getInt("autoBroadcaster.interval");

        if (interval <= 0)
            return;

        short type = getConfig().getShort("autoBroadcaster.type");

        if (!getConfig().getBoolean("autoBroadcaster.enabled"))
            return;

        new BukkitRunnable() {
            private int index = 0;

            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().isEmpty()) return;
                if (type == 1) { // SEQUENTIAL
                    List<String> keys = new ArrayList<>();
                    for (Object key : getConfig().getSection("autoBroadcaster.messages").getKeys()) {
                        keys.add((String) key);
                    }
                    if (keys.isEmpty()) return;

                    String messageId = keys.get(index);
                    broadcast(messageId);

                    index = (index + 1) % keys.size();
                } else if (type == 2) { // RANDOM
                    List<String> keys = new ArrayList<>();
                    for (Object key : getConfig().getSection("autoBroadcaster.messages").getKeys()) {
                        keys.add((String) key);
                    }
                    if (keys.isEmpty()) return;

                    String messageId = keys.get(new Random().nextInt(keys.size()));
                    broadcast(messageId);
                }
            }
        }.runTaskTimer(main, (interval * 20L) / 2, interval * 20L); // interval in seconds
    }

}
