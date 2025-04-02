package dev.losterixx.sCore

import dev.losterixx.sCore.features.autobroadcaster.BroadcastManager
import dev.losterixx.sCore.placeholderapi.CustomPlaceholders
import dev.losterixx.sCore.utils.ConfigManager
import dev.losterixx.sCore.utils.CoroutineUtils
import dev.losterixx.sCore.utils.RegisterManager
import dev.losterixx.sCore.utils.UpdateChecker
import net.kyori.adventure.text.minimessage.MiniMessage
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.nio.file.Files


class Main : JavaPlugin() {

    companion object {
        lateinit var instance: Main
            private set

        const val DEFAULT_PREFIX = "<#47E3A4><b>S-Core</b> <dark_gray>⚡ <gray>"
        val miniMessage = MiniMessage.miniMessage()

        lateinit var economy: Economy
            private set
    }

    override fun onEnable() {

        logger.info("Plugin is being enabled...")

        //-> Custom
        instance = this
        CoroutineUtils.init(instance)

        //-> Configs
        loadLangFiles()
        loadConfigFiles()
        logger.info("Loaded ${ConfigManager.getAllConfigs().size} configs!")

        //-> APIs
        if (setupEconomy()) {
            logger.info("Hooked into Vault!")
        } else {
            logger.warning("Vault could not be found! Economy-Features won't work.")
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            if (ConfigManager.getConfig("modules").getBoolean("custom-placeholders")) {
                CustomPlaceholders().register()
                logger.info("Hooked into PlaceholderAPI!")
            } else {
                logger.info("Custom Placeholders are disabled in modules.yml! Skipping PlaceholderAPI hook.")
            }
        } else {
            logger.warning("PlaceholderAPI could not be found! Placeholders won't work.")
        }

        //-> Features
        BroadcastManager.startBroadcasting()

        //-> Register
        RegisterManager.registerAll()

        //-> Update Checker
        if (ConfigManager.getConfig("config").getBoolean("updateChecker.consoleMessage")) {
            CoroutineUtils.launchAsync {
                if (!isLatestVersion()) {
                    logger.warning("You are not using the latest version of S-Core! Please update to the latest version.")
                    logger.warning("Latest version: ${UpdateChecker.getLatestGitHubRelease("Losterixx", "S-Core")}")
                    logger.warning("Your version: ${description.version}")
                } else {
                    logger.info("You are using the latest version of S-Core!")
                }
            }
        }

        logger.info("Plugin has been enabled!")

    }

    override fun onDisable() {

        CoroutineUtils.cancelAll()

        logger.info("Plugin has been disabled!")

    }


    fun loadConfigFiles() {
        ConfigManager.createConfig("config", "config.yml")
        ConfigManager.createConfig("modules", "modules.yml")
        ConfigManager.createConfig("data", "data.yml")
        ConfigManager.createConfig("custom-placeholders", "custom-placeholders.yml")

        loadLangFiles()
        val langFile = ConfigManager.getConfig("config").getString("langFile", null)
        if (langFile == null) {
            logger.warning("No language file specified in config.yml! Defaulting to english.yml.")
            config.set("langFile", "english")
            ConfigManager.saveConfig("config")
        }
        ConfigManager.createConfig(langFile, "lang/$langFile.yml", "lang")
        logger.info("Using language file: $langFile.yml")
    }

    fun loadLangFiles() {
        val langDirectory = dataFolder.toPath().resolve("lang")

        if (!Files.exists(langDirectory)) {
            Files.createDirectories(langDirectory)
        }

        val defaultLangFiles = listOf("english.yml", "german.yml")

        defaultLangFiles.forEach { fileName ->
            val langConfig = fileName.removeSuffix(".yml")
            ConfigManager.createConfig(langConfig, "lang/$fileName", "lang")
        }

        Files.list(langDirectory).filter { it.toString().endsWith(".yml") }.forEach { langFile ->
            val langConfig = langFile.fileName.toString().removeSuffix(".yml")
            if (!ConfigManager.existsConfig(langConfig)) {
                ConfigManager.createConfig(langConfig, "lang/${langFile.fileName}", "lang")
            }
        }
    }

    fun isLatestVersion(): Boolean {
        val currentVersion = description.version
        val latestVersion = UpdateChecker.getLatestGitHubRelease("Losterixx", "S-Core")
        return latestVersion != null && latestVersion == currentVersion
    }

    private fun setupEconomy(): Boolean {
        val vault = server.pluginManager.getPlugin("Vault") ?: return false
        val rsp = server.servicesManager.getRegistration(Economy::class.java) ?: return false
        economy = rsp.provider ?: return false
        return true
    }

}
