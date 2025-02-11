package dev.losterixx.sCore.paper.features.custommessages;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.paper.PaperMain;
import dev.losterixx.sCore.paper.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CustomMessagesListener implements Listener {

    private MiniMessage mm = PaperMain.mm;
    private PaperMain main = PaperMain.getInstance();
    private ConfigManager configManager = main.getConfigManager();
    private YamlDocument getConfig() { return configManager.getConfig("config"); }
    private String getPrefixString() { return getConfig().getString("prefix"); }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String message = getConfig().getString("customMessages.join", null);
        if (message != null) message = message.replaceAll("%player%", event.getPlayer().getName());
        if (message != null) message = message.replaceAll("%prefix%", getPrefixString());
        event.joinMessage(message == null ? null : mm.deserialize(message));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        String message = getConfig().getString("customMessages.quit", null);
        if (message != null) message = message.replaceAll("%player%", event.getPlayer().getName());
        if (message != null) message = message.replaceAll("%prefix%", getPrefixString());
        event.quitMessage(message == null ? null : mm.deserialize(message));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        String message = getConfig().getString("customMessages.death", null);
        if (message != null) message = message.replaceAll("%player%", event.getPlayer().getName());
        if (message != null) message = message.replaceAll("%prefix%", getPrefixString());
        event.deathMessage(message == null ? null : mm.deserialize(message));
    }

}
