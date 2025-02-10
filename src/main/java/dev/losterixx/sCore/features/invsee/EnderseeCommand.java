package dev.losterixx.sCore.features.invsee;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.Main;
import dev.losterixx.sCore.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class EnderseeCommand implements CommandExecutor, TabCompleter, Listener {

    private MiniMessage mm = Main.mm;
    private Main main = Main.getInstance();
    private ConfigManager configManager = main.getConfigManager();
    private YamlDocument getConfig() { return configManager.getConfig("config"); }
    private YamlDocument getMessages() { return configManager.getConfig("messages"); }
    private Component getPrefix() { return mm.deserialize(getConfig().getString("prefix")); }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPlayer"))));
            return false;
        }

        if (!sender.hasPermission("sCore.command.endersee.show") && !sender.hasPermission("sCore.command.endersee.modify")) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.endersee.usage"))));
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.playerNotFound"))));
            return false;
        }

        if (target.getUniqueId() == ((Player) sender).getUniqueId()) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.endersee.cannotUseOnSelf"))));
            return false;
        }

        ((Player) sender).setMetadata("invsee-target", new FixedMetadataValue(main, target.getName()));
        ((Player) sender).openInventory(target.getEnderChest());

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("sCore.command.endersee.show") && !sender.hasPermission("sCore.command.endersee.modify")) return completions;

        if (args.length == 0) {
            for (Player players : main.getServer().getOnlinePlayers()) {
                completions.add(players.getName());
            }
        } else if (args.length == 1) {
            for (Player players : main.getServer().getOnlinePlayers()) {
                if (players.getName().toLowerCase().startsWith(args[0].toLowerCase())) completions.add(players.getName());
            }
        }

        return completions;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!player.hasMetadata("invsee-target")) return;

        //if (!player.hasPermission("sCore.command.endersee.modify")) {
            event.setCancelled(true);
        //}
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (player.hasMetadata("invsee-target")) {
            player.removeMetadata("invsee-target", main);
        }
    }

}
