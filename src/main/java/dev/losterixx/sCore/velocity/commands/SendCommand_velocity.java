package dev.losterixx.sCore.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.velocity.VelocityMain;
import dev.losterixx.sCore.velocity.utils.ConfigManager_velocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SendCommand_velocity implements SimpleCommand {

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

        if (!source.hasPermission("sCore.command.send")) {
            source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return;
        }

        if (args.length != 2) {
            source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.send.usage"))));
            return;
        }

        String target = args[0];
        String serverName = args[1];
        Optional<RegisteredServer> targetServer = getProxy().getServer(serverName);

        if (targetServer.isEmpty()) {
            source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.send.serverNotFound").replaceAll("%server%", serverName))));
            return;
        }

        if (target.equalsIgnoreCase("*")) {
            for (Player players : getProxy().getAllPlayers()) {
                players.createConnectionRequest(targetServer.get()).connect();
            }
            source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.send.successAll").replaceAll("%server%", serverName))));
        } else {
            Optional<Player> player = getProxy().getPlayer(target);
            if (player.isPresent()) {
                player.get().createConnectionRequest(targetServer.get()).connect();
                source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.send.success").replaceAll("%player%", target).replaceAll("%server%", serverName))));
            } else {
                source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.playerNotFound"))));
            }
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> suggestions = new ArrayList<>();
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!source.hasPermission("sCore.command.send")) return suggestions;

        if (args.length == 0) {
            for (Player players : getProxy().getAllPlayers()) {
                suggestions.add(players.getUsername());
                suggestions.add("*");
            }
        } else if (args.length == 1) {
            for (Player players : getProxy().getAllPlayers()) {
                if (players.getUsername().toLowerCase().startsWith(args[0].toLowerCase())) suggestions.add(players.getUsername());
                if ("*".equals(args[0])) suggestions.add("*");
            }
        } else if (args.length == 2) {
            for (RegisteredServer servers : getProxy().getAllServers()) {
                if (servers.getServerInfo().getName().toLowerCase().startsWith(args[1].toLowerCase())) suggestions.add(servers.getServerInfo().getName());
            }
        }

        return suggestions;
    }

}
