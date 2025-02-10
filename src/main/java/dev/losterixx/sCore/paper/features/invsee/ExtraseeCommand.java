package dev.losterixx.sCore.paper.features.invsee;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.paper.PaperMain;
import dev.losterixx.sCore.paper.utils.ConfigManager;
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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class ExtraseeCommand implements CommandExecutor, TabCompleter, Listener {

    private MiniMessage mm = PaperMain.mm;
    private PaperMain main = PaperMain.getInstance();
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

        if (!sender.hasPermission("sCore.command.extrasee.show")) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.extrasee.usage"))));
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.playerNotFound"))));
            return false;
        }

        if (target.getUniqueId() == ((Player) sender).getUniqueId()) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.extrasee.cannotUseOnSelf"))));
            return false;
        }

        ((Player) sender).setMetadata("invsee-target", new FixedMetadataValue(main, target.getName()));
        Inventory gui = Bukkit.createInventory(null, InventoryType.HOPPER, mm.deserialize("<reset>Extra"));
        gui.setItem(0, target.getInventory().getHelmet());
        gui.setItem(1, target.getInventory().getChestplate());
        gui.setItem(2, target.getInventory().getLeggings());
        gui.setItem(3, target.getInventory().getBoots());
        gui.setItem(4, target.getInventory().getItemInOffHand());
        ((Player) sender).openInventory(gui);

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("sCore.command.extrasee.show")) return completions;

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

        //if (!player.hasPermission("sCore.command.extrasee.modify")) {
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
