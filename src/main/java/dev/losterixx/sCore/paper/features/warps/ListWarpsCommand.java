package dev.losterixx.sCore.paper.features.warps;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.losterixx.sCore.paper.PaperMain;
import dev.losterixx.sCore.paper.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListWarpsCommand implements CommandExecutor {

    private MiniMessage mm = PaperMain.mm;
    private PaperMain main = PaperMain.getInstance();
    private ConfigManager configManager = main.getConfigManager();
    private YamlDocument getConfig() { return configManager.getConfig("config"); }
    private YamlDocument getMessages() { return configManager.getConfig("messages"); }
    private YamlDocument getData() { return configManager.getConfig("data"); }
    private Component getPrefix() { return mm.deserialize(getConfig().getString("prefix")); }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPlayer"))));
            return false;
        }

        if (!sender.hasPermission("sCore.command.listwarps")) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return false;
        }

        if (args.length != 0) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.listwarps.usage"))));
            return false;
        }

        Player player = (Player) sender;

        Section warpsSection = getData().getSection("warps");

        if (warpsSection == null) {
            player.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.listwarps.noWarps"))));
            return false;
        }

        player.sendMessage(mm.deserialize(getMessages().getString("commands.listwarps.header")));
        for (Object warpName : warpsSection.getKeys()) {
            if (!(warpName instanceof String)) continue;
            String key = (String) warpName;
            player.sendMessage(mm.deserialize(getMessages().getString("commands.listwarps.entry").replaceAll("%warp%", key)));
        }
        player.sendMessage(mm.deserialize(getMessages().getString("commands.listwarps.footer")));

        return false;
    }

}
