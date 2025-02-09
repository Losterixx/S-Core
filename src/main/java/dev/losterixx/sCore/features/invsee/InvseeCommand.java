package dev.losterixx.sCore.features.invsee;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.Main;
import dev.losterixx.sCore.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InvseeCommand implements CommandExecutor, TabCompleter {

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

        if (!sender.hasPermission("sCore.command.invsee.show") && !sender.hasPermission("sCore.command.invsee.modify")) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.noPerms"))));
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.invsee.usage"))));
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("general.playerNotFound"))));
            return false;
        }

        Inventory targetInventory = target.getInventory();

        // ToDo: Implement ItemBuilder and set itemmeta
        // ToDo: Make this gui customizable in config.yml
        // TODo: Add Listener to block modifying items in the inventory without permission
        Inventory gui = Bukkit.createInventory(null, 9 * 6, mm.deserialize(getConfig().getString("invsee.guiTitle").replace("%player%", target.getName())));

        for (int i = 0; i < 36; i++) {
            gui.setItem(i, targetInventory.getItem(i));
        }

        for (int i = 0; i < 4; i++) {
            gui.setItem(46 + i, target.getInventory().getArmorContents()[i]);
        }

        gui.setItem(51, target.getInventory().getItemInOffHand());
        gui.setItem(52, target.getItemOnCursor());

        if (gui.getItem(51) == null) {
            gui.setItem(51, new ItemStack(Material.BARRIER));
        }
        if (gui.getItem(52) == null) {
            gui.setItem(52, new ItemStack(Material.BARRIER));
        }

        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) != null && gui.getItem(i).getType() != Material.AIR) continue;
            gui.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }

        ((Player) sender).openInventory(gui);

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("sCore.command.invsee.show") && !sender.hasPermission("sCore.command.invsee.modify")) return completions;

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

}
