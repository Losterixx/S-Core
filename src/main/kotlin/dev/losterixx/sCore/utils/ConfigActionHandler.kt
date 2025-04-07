package dev.losterixx.sCore.utils

import com.google.common.io.ByteStreams
import com.nexomc.nexo.api.NexoItems
import dev.losterixx.sCore.Main
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.time.Duration

object ConfigActionHandler {

    private val mm = Main.Companion.miniMessage
    private val main = Main.Companion.instance

    private val COLOR_CODE_MAP = mapOf(
        '0' to "black",
        '1' to "dark_blue",
        '2' to "dark_green",
        '3' to "dark_aqua",
        '4' to "dark_red",
        '5' to "dark_purple",
        '6' to "gold",
        '7' to "gray",
        '8' to "dark_gray",
        '9' to "blue",
        'a' to "green",
        'b' to "aqua",
        'c' to "red",
        'd' to "light_purple",
        'e' to "yellow",
        'f' to "white",
        'k' to "obfuscated",
        'l' to "bold",
        'm' to "strikethrough",
        'n' to "underline",
        'o' to "italic",
        'r' to "reset"
    )

    fun executeActions(player: Player, actions: List<String>) {
        for (action in actions) {
            try {
                val parts = action.split("]", limit = 2)
                val type = parts[0].replace("[", "").uppercase()
                val content = parts.getOrNull(1)?.trim() ?: ""

                when (type) {

                    "MESSAGE", "MSG" -> {
                        player.sendMessage(parseMessageAsComponent(player, content))
                    }

                    "BROADCAST", "BR" -> {
                        Bukkit.broadcast(parseMessageAsComponent(player, content))
                    }

                    "SERVER", "SEND_TO_SERVER", "SEND_SERVER" -> {
                        execute(main, player, content.split(" ")[0])
                    }

                    "SEND_ALL", "MESSAGE_ALL" -> {
                        Bukkit.getOnlinePlayers().forEach {
                            it.sendMessage(parseMessageAsComponent(player, content))
                        }
                    }

                    "ACTION_BAR" -> {
                        player.sendActionBar(parseMessageAsComponent(player, content))
                    }

                    "TITLE" -> {
                        val titleParts = content.split(";", limit = 5)
                        val title = titleParts.getOrNull(0) ?: ""
                        val subtitle = titleParts.getOrNull(1) ?: ""
                        val fadeIn = titleParts.getOrNull(2)?.toIntOrNull() ?: 10
                        val stay = titleParts.getOrNull(3)?.toIntOrNull() ?: 40
                        val fadeOut = titleParts.getOrNull(4)?.toIntOrNull() ?: 10

                        player.showTitle(
                            Title.title(
                                parseMessageAsComponent(player, title),
                                parseMessageAsComponent(player, subtitle),
                                Title.Times.times(
                                    Duration.ofMillis(fadeIn * 50L),
                                    Duration.ofMillis(stay * 50L),
                                    Duration.ofMillis(fadeOut * 50L)
                                )
                            )
                        )
                    }

                    "PLAY_SOUND", "SOUND" -> {
                        val soundParts = content.split(";")
                        val sound = runCatching { Sound.valueOf(soundParts[0].uppercase()) }.getOrElse {
                            main.logger.warning("Invalid sound: ${soundParts[0].uppercase()}")
                            return
                        }
                        val volume = soundParts.getOrNull(1)?.toFloatOrNull() ?: 1.0f
                        val pitch = soundParts.getOrNull(2)?.toFloatOrNull() ?: 1.0f

                        player.playSound(player.location, sound, volume, pitch)
                    }

                    "POTION_EFFECT", "EFFECT" -> {
                        val parts = content.split(";")
                        val type = PotionEffectType.getByName(parts[0].uppercase()) ?: return
                        val duration = (parts.getOrNull(1)?.toIntOrNull() ?: 10) * 20
                        val amplifier = (parts.getOrNull(2)?.toIntOrNull() ?: 1) - 1
                        val ambient = parts.getOrNull(3)?.toBooleanStrictOrNull() ?: false
                        val particles = parts.getOrNull(4)?.toBooleanStrictOrNull() ?: true
                        val icon = parts.getOrNull(5)?.toBooleanStrictOrNull() ?: true

                        player.addPotionEffect(PotionEffect(type, duration, amplifier, ambient, particles, icon))
                    }

                    "GIVE_ITEM", "GIVE" -> {
                        val parts = content.split(";")
                        val amount = parts.getOrNull(1)?.toIntOrNull() ?: 1

                        val item = if (parts[0].startsWith("nexo:")) NexoItems.itemFromId(parts[0])?.build()
                        else runCatching { ItemStack(Material.valueOf(parts[0].uppercase())) }.getOrNull()

                        if (item == null) {
                            main.logger.warning("Invalid item: ${parts[0]}")
                            return
                        }

                        item.amount = amount
                        player.inventory.addItem(item)
                    }

                    "PLAYER_COMMAND", "PLAYER_CMD" -> {
                        player.performCommand(parseMessageAsString(player, content))
                    }

                    "CONSOLE_COMMAND", "CONSOLE_CMD", "SERVER_COMMAND", "SERVER_CMD" -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parseMessageAsString(player, content))
                    }

                    "CLOSE", "CLOSE_GUI" -> {
                        player.closeInventory()
                    }

                    else -> {
                        main.logger.warning("Unknown action type: $type")
                    }
                }
            } catch (e: Exception) {
                main.logger.warning("Failed to execute action '$action': ${e.message}")
            }
        }
    }

    private fun parseMessageAsComponent(player: Player, message: String): Component {
        var msg = PlaceholderAPI.setPlaceholders(player, message)
        val pattern = Regex("(?i)[&§]([0-9a-fk-or])")
        msg = pattern.replace(msg) {
            val code = it.groupValues[1].lowercase()[0]
            COLOR_CODE_MAP[code]?.let { tag -> "<$tag>" } ?: it.value
        }
        return mm.deserialize(msg)
    }

    private fun parseMessageAsString(player: Player, message: String): String {
        val papiMessage = PlaceholderAPI.setPlaceholders(player, message)
        return papiMessage
    }

    private fun execute(plugin: Main, player: Player, data: String) {
        val out = ByteStreams.newDataOutput()
        out.writeUTF("ConnectOther")
        out.writeUTF(player.name)
        out.writeUTF(data)
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray())
    }
}