package dev.losterixx.sCore.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.general.GeneralMain;
import dev.losterixx.sCore.velocity.utils.ConfigManager_velocity;
import dev.losterixx.sCore.velocity.VelocityMain;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.List;

public class SCoreCommand_velocity implements SimpleCommand {

    private MiniMessage mm = VelocityMain.mm;
    private ConfigManager_velocity configManager = VelocityMain.getInstance().getConfigManager();
    private YamlDocument getConfig() { return configManager.getConfig("velocity-config"); }
    private YamlDocument getMessages() { return configManager.getConfig("velocity-messages"); }
    private Component getPrefix() { return mm.deserialize(getConfig().getString("prefix")); }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!source.hasPermission("sCore.admin")) {
            source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return;
        }

        if (args.length != 1) {
            source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.vscore.usage"))));
            return;
        }

        switch (args[0].toLowerCase()) {

            default -> {
                source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.vscore.usage"))));
            }

            case "about" -> {
                source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.vscore.about").replaceAll("%version%", GeneralMain.PLUGIN_VERSION))));
            }

            case "reload", "rl" -> {
                source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.vscore.reload.reloading"))));
                long now = System.currentTimeMillis();
                configManager.reloadAllConfigs();
                long timeElapsedMillis = System.currentTimeMillis() - now;
                double timeElapsedSeconds = timeElapsedMillis / 1000.0;
                String formattedTimeElapsed = String.format("%.3f", timeElapsedSeconds).replaceAll(",", ".");
                source.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.vscore.reload.reloaded").replaceAll("%time%", formattedTimeElapsed))));
            }

        }

    }

    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> suggestions = new ArrayList<>();
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!source.hasPermission("sCore.admin")) return suggestions;

        if (args.length == 0) {
            suggestions.add("about");
            suggestions.add("reload");
        } else if (args.length == 1) {
            if ("about".startsWith(args[0].toLowerCase())) suggestions.add("about");
            if ("reload".startsWith(args[0].toLowerCase())) suggestions.add("reload");
        }

        return suggestions;
    }

}
