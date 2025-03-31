package dev.losterixx.sCore.utils

import dev.losterixx.sCore.Main
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.OfflinePlayer
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType

class ItemBuilder(private val material: Material) {

    private val mm: MiniMessage = Main.miniMessage
    private val itemStack: ItemStack = ItemStack(material)
    private val itemMeta: ItemMeta = itemStack.itemMeta!!

    fun setItemname(itemname: String) = apply {
        itemMeta.displayName(mm.deserialize(itemname).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
    }

    fun setDisplayname(displayname: String) = apply {
        itemMeta.displayName(mm.deserialize(displayname).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
    }

    fun setCustomModelData(i: Int) = apply {
        itemMeta.setCustomModelData(i)
    }

    fun setMetaData(key: NamespacedKey, value: Boolean) = apply {
        itemMeta.persistentDataContainer.set(key, PersistentDataType.BOOLEAN, value)
    }
    fun setMetaData(key: NamespacedKey, value: String) = apply {
        itemMeta.persistentDataContainer.set(key, PersistentDataType.STRING, value)
    }

    fun setLore(vararg s: String) = apply {
        val lore = s.map { mm.deserialize(it).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE) }
        itemMeta.lore(lore)
    }

    fun setAmount(i: Int) = apply {
        itemStack.amount = i
    }

    fun setUnbreakable(s: Boolean) = apply {
        itemMeta.isUnbreakable = s
    }

    fun addItemFlags(vararg s: ItemFlag) = apply {
        itemMeta.addItemFlags(*s)
    }

    fun addEnchantment(e: Enchantment, i: Int) = apply {
        itemMeta.addEnchant(e, i, true)
    }

    fun removeEnchantment(e: Enchantment) = apply {
        itemMeta.removeEnchant(e)
    }

    fun setDurability(durability: Int) = apply {
        val meta = itemStack.itemMeta
        if (meta is Damageable) {
            meta.damage = durability
            itemStack.itemMeta = meta
        } else {
            throw IllegalArgumentException("[ItemBuilder] ItemMeta is not Damageable")
        }
    }

    fun addAttributeModifier(attribute: Attribute, modifier: AttributeModifier) = apply {
        itemMeta.addAttributeModifier(attribute, modifier)
    }

    fun removeAttributeModifier(attribute: Attribute) = apply {
        itemMeta.removeAttributeModifier(attribute)
    }

    fun setHideTooltip(enabled: Boolean) = apply {
        itemMeta.isHideTooltip = enabled
    }

    fun setHeadOwner(player: OfflinePlayer) = apply {
        if (itemStack.type == Material.PLAYER_HEAD) {
            val skullMeta = itemMeta as SkullMeta
            skullMeta.owningPlayer = player
        } else {
            throw IllegalArgumentException("[ItemBuilder] Item is not a PLAYER_HEAD")
        }
    }

    fun build(): ItemStack {
        itemStack.itemMeta = itemMeta
        return itemStack
    }

    override fun toString(): String {
        return "ItemBuilder(itemMeta=$itemMeta, itemStack=$itemStack)"
    }
}