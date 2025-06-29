package dev.losterixx.sCore.features.autobroadcaster

import dev.losterixx.sCore.Main
import dev.losterixx.sCore.utils.ConfigManager
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random


object BroadcastManager {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    fun broadcast(messageId: String?) {
        if (!getConfig().contains("autoBroadcaster.messages.$messageId")) {
            main.logger.warning("Message with ID $messageId could not be found!")
            return
        }

        val messagesToSent = getConfig().getStringList("autoBroadcaster.messages.$messageId")

        if (messagesToSent.isEmpty()) {
            main.logger.warning("Message with ID $messageId is empty!")
            return
        }

        for (msg in messagesToSent) {
            var message = msg
            if (message.replace(" ", "").isEmpty()) {
                message = "<gray> "
            }

            for (player in Bukkit.getOnlinePlayers()) {
                player.sendMessage((mm.deserialize(message
                    .replace("%prefix%", getPrefix()))))
            }

            Bukkit.getConsoleSender().sendMessage(mm.deserialize(message
                .replace("%prefix%", getPrefix())))
        }

        if (getConfig().getBoolean("autoBroadcaster.sound.enabled")) {
            val soundName = getConfig().getString("autoBroadcaster.sound.sound")?.uppercase()?.replace('.', '_') ?: "ENTITY_PLAYER_LEVELUP"
            val sound = try {
                Sound.valueOf(soundName)
            } catch (e: Exception) {
                main.logger.warning("Invalid sound-name for autoBroadcaster: $soundName. Using default sound.")
                Sound.ENTITY_PLAYER_LEVELUP
            }
            val volume = getConfig().getDouble("autoBroadcaster.sound.volume")
            val pitch = getConfig().getDouble("autoBroadcaster.sound.pitch")

            for (player in Bukkit.getOnlinePlayers()) {
                player.playSound(player, sound, SoundCategory.MASTER, volume.toFloat(), pitch.toFloat())
            }
        }
    }

    fun startBroadcasting() {
        if (!getConfig().getBoolean("autoBroadcaster.enabled")) return

        val interval = getConfig().getInt("autoBroadcaster.interval")
        val type = getConfig().getByte("autoBroadcaster.type")

        if (interval <= 0) return

        object : BukkitRunnable() {
            private var index = 0

            override fun run() {
                if (Bukkit.getOnlinePlayers().isEmpty()) return
                if (type.toInt() == 1) { // SEQUENTIAL
                    val keys: MutableList<String?> = ArrayList<String?>()
                    for (key in getConfig().getSection("autoBroadcaster.messages").getKeys()) {
                        keys.add(key as String?)
                    }
                    if (keys.isEmpty()) return

                    val messageId = keys[index]
                    broadcast(messageId)

                    index = (index + 1) % keys.size
                } else if (type.toInt() == 2) { // RANDOM
                    val keys: MutableList<String?> = ArrayList<String?>()
                    for (key in getConfig().getSection("autoBroadcaster.messages").getKeys()) {
                        keys.add(key as String?)
                    }
                    if (keys.isEmpty()) return

                    val messageId = keys[Random.nextInt(keys.size)]
                    broadcast(messageId)
                }
            }
        }.runTaskTimer(main, (interval * 20L) / 2, interval * 20L) // interval in seconds
    }

}