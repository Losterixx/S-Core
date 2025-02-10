package dev.losterixx.sCore.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.velocity.VelocityMain;
import dev.losterixx.sCore.velocity.utils.ConfigManager_velocity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.List;

public class AlertCommand_velocity implements SimpleCommand {

    private MiniMessage mm = VelocityMain.mm;
    private ConfigManager_velocity configManager = VelocityMain.getInstance().getConfigManager();
    private YamlDocument getConfig() { return configManager.getConfig("velocity-config"); }
    private YamlDocument getMessages() { return configManager.getConfig("velocity-messages"); }
    private Component getPrefix() { return mm.deserialize(getConfig().getString("prefix")); }
    private ProxyServer getProxy() { return VelocityMain.getInstance().getProxy(); }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!source.hasPermission("sCore.command.alert")) {
            source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return;
        }

        if (args.length == 0) {
            source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.alert.usage"))));
            return;
        }

        String message = String.join(" ", args);

        for (Player players : getProxy().getAllPlayers()) {
            for (String s : getMessages().getStringList("commands.alert.alert.messages")) {
                players.sendMessage(mm.deserialize(s.replaceAll("%message%", message)));
            }
            if (getConfig().getBoolean("commands.alert.alert.sound.enabled")) {
                String soundName = getConfig().getString("commands.alert.alert.sound.name", "ENTITY_PLAYER_LEVELUP");
                float volume = getConfig().getFloat("commands.alert.alert.sound.volume");
                float pitch = getConfig().getFloat("commands.alert.alert.sound.pitch");

                players.playSound(Sound.sound(Key.key(soundName), Sound.Source.MASTER, volume, pitch));
            }
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> suggestions = new ArrayList<>();
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!source.hasPermission("sCore.command.alert")) return suggestions;

        if (args.length == 0) {
            suggestions.add("<message>");
        }
        return suggestions;
    }

}
