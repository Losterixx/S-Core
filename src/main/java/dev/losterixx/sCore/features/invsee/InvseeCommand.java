package dev.losterixx.sCore.features.invsee;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.Main;
import dev.losterixx.sCore.utils.ConfigManager;
import dev.losterixx.sCore.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class InvseeCommand implements CommandExecutor, TabCompleter, Listener {

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

        if (target.getUniqueId() == ((Player) sender).getUniqueId()) {
            sender.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.invsee.cannotUseOnSelf"))));
            return false;
        }

        Inventory targetInventory = target.getInventory();

        Inventory gui = Bukkit.createInventory(null, 9 * 6, mm.deserialize(getConfig().getString("invsee.guiTitle").replace("%player%", target.getName())));

        for (int i = 0; i < 36; i++) {
            ItemStack item = targetInventory.getItem(i);
            gui.setItem(i, item);
        }

        for (int i = 0; i < 4; i++) {
            ItemStack armorItem = target.getInventory().getArmorContents()[i];
            gui.setItem(46 + i, armorItem);
        }

        ItemStack offHandItem = target.getInventory().getItemInOffHand();
        gui.setItem(51, offHandItem);

        ItemStack cursorItem = target.getItemOnCursor();
        gui.setItem(52, cursorItem);

        if (getConfig().getBoolean("invsee.filler.enabled", true)) {
            for (int i = 36; i <= 45; i++) {
                gui.setItem(i, new ItemBuilder(Material.valueOf(getConfig().getString("invsee.filler.material")))
                        .setItemname(getConfig().getString("invsee.filler.name"))
                        .setDisplayname(getConfig().getString("invsee.filler.name"))
                        .setLore(getConfig().getStringList("invsee.filler.lore"))
                        .setHideTooltip(getConfig().getBoolean("invsee.filler.hideTooltip"))
                        .setMetaData(new NamespacedKey(Main.getInstance(), "isBlocked"), true)
                        .build()
                );
            }
            gui.setItem(45, new ItemBuilder(Material.valueOf(getConfig().getString("invsee.filler.material")))
                    .setItemname(getConfig().getString("invsee.filler.name"))
                    .setDisplayname(getConfig().getString("invsee.filler.name"))
                    .setLore(getConfig().getStringList("invsee.filler.lore"))
                    .setHideTooltip(getConfig().getBoolean("invsee.filler.hideTooltip"))
                    .setMetaData(new NamespacedKey(Main.getInstance(), "isBlocked"), true)
                    .build()
            );
            gui.setItem(50, new ItemBuilder(Material.valueOf(getConfig().getString("invsee.filler.material")))
                    .setItemname(getConfig().getString("invsee.filler.name"))
                    .setDisplayname(getConfig().getString("invsee.filler.name"))
                    .setLore(getConfig().getStringList("invsee.filler.lore"))
                    .setHideTooltip(getConfig().getBoolean("invsee.filler.hideTooltip"))
                    .setMetaData(new NamespacedKey(Main.getInstance(), "isBlocked"), true)
                    .build()
            );
            gui.setItem(53, new ItemBuilder(Material.valueOf(getConfig().getString("invsee.filler.material")))
                    .setItemname(getConfig().getString("invsee.filler.name"))
                    .setDisplayname(getConfig().getString("invsee.filler.name"))
                    .setLore(getConfig().getStringList("invsee.filler.lore"))
                    .setHideTooltip(getConfig().getBoolean("invsee.filler.hideTooltip"))
                    .setMetaData(new NamespacedKey(Main.getInstance(), "isBlocked"), true)
                    .build()
            );
        }

        ((Player) sender).setMetadata("invsee-target", new FixedMetadataValue(main, target.getName()));
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

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!player.hasMetadata("invsee-target")) return;

        if (!player.hasPermission("sCore.command.invsee.modify")) {
            event.setCancelled(true);
            if (!player.hasPermission("sCore.command.invsee.show")) {
                player.closeInventory();
            }
            return;
        }

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null && currentItem.getType() != Material.AIR) {
            if (currentItem.getType() == Material.matchMaterial(getConfig().getString("invsee.filler.material")) &&
                    currentItem.hasItemMeta() &&
                    currentItem.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "isBlocked"), PersistentDataType.BOOLEAN)) {

                event.setCancelled(true);
                return;
            }
        }

        Player target = Bukkit.getPlayer(player.getMetadata("invsee-target").get(0).asString());
        if (target != null && target.isOnline()) {
            Inventory clickedInventory = event.getClickedInventory();
            ItemStack cursorItem = event.getCursor();

            if (clickedInventory != null && cursorItem != null && cursorItem.getType() != Material.AIR) {
                if (clickedInventory.equals(player.getInventory())) {
                    // Player's own inventory
                    if (event.getSlot() >= 0 && event.getSlot() <= 35) {
                        player.getInventory().setItem(event.getSlot(), cursorItem);
                    } else if (event.getSlot() >= 46 && event.getSlot() <= 49) {
                        switch (event.getSlot()) {
                            case 46 -> player.getInventory().setBoots(cursorItem);
                            case 47 -> player.getInventory().setLeggings(cursorItem);
                            case 48 -> player.getInventory().setChestplate(cursorItem);
                            case 49 -> player.getInventory().setHelmet(cursorItem);
                        }
                    } else if (event.getSlot() == 51) {
                        player.getInventory().setItemInOffHand(cursorItem);
                    } else if (event.getSlot() == 52) {
                        player.setItemOnCursor(cursorItem);
                    } else return;
                } else if (clickedInventory.equals(target.getInventory())) {
                    // Target player's inventory
                    if (event.getSlot() >= 0 && event.getSlot() <= 35) {
                        target.getInventory().setItem(event.getSlot(), cursorItem);
                    } else if (event.getSlot() >= 46 && event.getSlot() <= 49) {
                        switch (event.getSlot()) {
                            case 46 -> target.getInventory().setBoots(cursorItem);
                            case 47 -> target.getInventory().setLeggings(cursorItem);
                            case 48 -> target.getInventory().setChestplate(cursorItem);
                            case 49 -> target.getInventory().setHelmet(cursorItem);
                        }
                    } else if (event.getSlot() == 51) {
                        target.getInventory().setItemInOffHand(cursorItem);
                    } else if (event.getSlot() == 52) {
                        target.setItemOnCursor(cursorItem);
                    } else return;
                }
            }
        } else {
            player.sendMessage(getPrefix().append(mm.deserialize(getMessages().getString("commands.invsee.playerOffline"))));
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (player.hasMetadata("invsee-target")) {
            player.removeMetadata("invsee-target", main);
        }
    }

}
