package dev.losterixx.sCore.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.velocity.utils.ConfigManager_velocity;
import dev.losterixx.sCore.velocity.VelocityMain;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class LobbyCommand_velocity implements SimpleCommand {

    private MiniMessage mm = VelocityMain.mm;
    private ConfigManager_velocity configManager = VelocityMain.getInstance().getConfigManager();
    private YamlDocument getConfig() { return configManager.getConfig("velocity-config"); }
    private YamlDocument getMessages() { return configManager.getConfig("velocity-messages"); }
    private Component getPrefix() { return mm.deserialize(getConfig().getString("prefix")); }
    private RegisteredServer getLobbyServer() { return VelocityMain.getInstance().getProxy().getServer(getConfig().getString("lobbyCommand.lobbyServer")).orElse(null); }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!(source instanceof Player)) {
            source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPlayer"))));
            return;
        }

        if (!source.hasPermission("sCore.command.lobby")) {
            source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return;
        }

        if (args.length != 0) {
            source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.lobby.usage"))));
            return;
        }

        source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.lobby.send.sending"))));
        try {
            ((Player) source).createConnectionRequest(getLobbyServer()).connect();
            source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.lobby.send.sent"))));
        } catch (Exception e) {
            source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.lobby.send.error"))));
        }

    }

}
