package dev.losterixx.sCore.utils;

import dev.losterixx.sCore.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class ItemEditor {

    private MiniMessage mm = Main.getInstance().mm;

    private ItemMeta itemMeta;
    private ItemStack itemStack;

    public ItemEditor(ItemStack item) {
        this.itemStack = item;
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemEditor setItemname(String itemname) {
        this.itemMeta.itemName(mm.deserialize(itemname).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        return this;
    }

    public ItemEditor setDisplayname(String displayname) {
        this.itemMeta.displayName(mm.deserialize(displayname).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        return this;
    }

    public ItemEditor setCustomModelData(int i) {
        this.itemMeta.setCustomModelData(i);
        return this;
    }

    public ItemEditor setMetaData(NamespacedKey key, boolean value) {
        this.itemMeta.getPersistentDataContainer().set(
                key,
                PersistentDataType.BOOLEAN, value
        );
        return this;
    }

    public ItemEditor setLore(String... s) {
        List<Component> lore = Arrays.stream(s)
                .map(mm::deserialize)
                .map(loreLine -> loreLine.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                .toList();
        this.itemMeta.lore(lore);
        return this;
    }

    public ItemEditor setLore(List<String> s) {
        List<Component> lore = s.stream()
                .map(mm::deserialize)
                .map(loreLine -> loreLine.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                .toList();
        this.itemMeta.lore(lore);
        return this;
    }

    public ItemEditor setAmount(Integer i) {
        this.itemStack.setAmount(i);
        return this;
    }

    public ItemEditor setUnbreakable(boolean s) {
        this.itemMeta.setUnbreakable(s);
        return this;
    }

    public ItemEditor setHideTooltip(boolean s) {
        this.itemMeta.setHideTooltip(s);
        return this;
    }

    public ItemEditor addItemFlags(ItemFlag... s) {
        this.itemMeta.addItemFlags(s);
        return this;
    }

    public ItemEditor addEnchantment(Enchantment e, Integer i) {
        this.itemMeta.addEnchant(e, i, true);
        return this;
    }

    public String toString() {
        return "ItemEditor{itemMeta=" + this.itemMeta + ", itemStack=" + this.itemStack + '}';
    }

    public ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }
}