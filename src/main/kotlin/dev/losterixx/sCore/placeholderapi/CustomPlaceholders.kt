package dev.losterixx.sCore.placeholderapi

import dev.losterixx.sCore.Main
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.floor
import kotlin.math.pow

class CustomPlaceholders : PlaceholderExpansion() {

    private val main = Main.instance
    private val configManager = Main.configManager
    private fun getCustomPlaceholders() = configManager.getConfig("custom-placeholders")
    private val economy = Main.economy


    override fun getIdentifier(): String {
        return "sCore"
    }

    override fun getAuthor(): String {
        return "Losterixx"
    }

    override fun getVersion(): String {
        return "1.0"
    }

    override fun onPlaceholderRequest(player: Player?, identifier: String): String? {
        if (player == null) return null

        when(identifier.lowercase()) {

            "player_world" -> {
                if (!getCustomPlaceholders().getBoolean("player.world.enabled")) return getCustomPlaceholders().getString("general.disabledValue")
                if (player.world == null) return getCustomPlaceholders().getString("general.errorValue")

                if (getCustomPlaceholders().getSection("player.world.translations").contains(player.world.name.lowercase())) {
                    return getCustomPlaceholders().getString("player.world.translations.${player.world.name.lowercase()}")
                }

                return player.world.name
            }

            "player_money" -> {
                if (!getCustomPlaceholders().getBoolean("player.money.enabled")) return getCustomPlaceholders().getString("general.disabledValue")
                if (economy == null) return getCustomPlaceholders().getString("general.errorValue")

                val balance = economy.getBalance(player)
                val fractionalDigits = getCustomPlaceholders().getInt("player.money.fractionalDigits", 2)
                val roundedBalance = (balance * 10.0.pow(fractionalDigits)).let { floor(it) / 10.0.pow(fractionalDigits) }
                val format = "%.${fractionalDigits}f"

                return format.format(roundedBalance)
            }

            "player_money_formatted" -> {
                if (!getCustomPlaceholders().getBoolean("player.money_formatted.enabled")) return getCustomPlaceholders().getString("general.disabledValue")
                if (economy == null) return getCustomPlaceholders().getString("general.errorValue")

                val balance = economy.getBalance(player)
                val format = getCustomPlaceholders().getString("player.money_formatted.decimalFormat.format", "#,###.##")!!
                val localeStr = getCustomPlaceholders().getString("player.money_formatted.decimalFormat.locale", "ENGLISH")!!
                val locale = Locale.forLanguageTag(localeStr.uppercase())
                val decimalFormat = (NumberFormat.getNumberInstance(locale) as DecimalFormat).apply { applyPattern(format) }
                val roundedBalance = (balance * 10.0.pow(decimalFormat.maximumFractionDigits)).let { floor(it) / 10.0.pow(decimalFormat.maximumFractionDigits) }

                return decimalFormat.format(roundedBalance)
            }

        }

        return null
    }
}