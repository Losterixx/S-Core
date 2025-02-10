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
import java.util.Optional;

public class FindCommand_velocity implements SimpleCommand {

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

        if (!source.hasPermission("sCore.command.find")) {
            source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return;
        }

        if (args.length != 1) {
            source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.find.usage"))));
            return;
        }

        String playerName = args[0];
        Optional<Player> player = getProxy().getPlayer(playerName);

        if (player.isPresent()) {
            player.get().getCurrentServer().ifPresentOrElse(
                    server -> source.sendMessage(getPrefix().append(mm.deserialize(
                            getMessages().getString("commands.find.success")
                                    .replaceAll("%player%", playerName)
                                    .replaceAll("%server%", server.getServerInfo().getName())
                    ))),
                    () -> source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.find.serverNotFound")
                            .replace("%player%", playerName))))
            );
        } else {
            source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.playerNotFound")
                    .replace("%player%", playerName))));
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> suggestions = new ArrayList<>();
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!source.hasPermission("sCore.command.find")) return suggestions;

        if (args.length == 0) {
            for (Player players : getProxy().getAllPlayers()) {
                suggestions.add(players.getUsername());
            }
        } else if (args.length == 1) {
            for (Player players : getProxy().getAllPlayers()) {
                if (players.getUsername().toLowerCase().startsWith(args[0].toLowerCase())) suggestions.add(players.getUsername());
            }
        }

        return suggestions;
    }

}
