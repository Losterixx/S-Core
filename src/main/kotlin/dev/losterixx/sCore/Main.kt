package dev.losterixx.sCore

import dev.losterixx.sCore.placeholderapi.CustomPlaceholders
import dev.losterixx.sCore.utils.ConfigManager
import dev.losterixx.sCore.utils.RegisterManager
import net.kyori.adventure.text.minimessage.MiniMessage
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.nio.file.Files


class Main : JavaPlugin() {

    companion object {
        lateinit var instance: Main
            private set

        const val DEFAULT_PREFIX = "<gradient:#2D734E:#4DC484><b>S-Core</b> <dark_gray>⚡ <gray>"
        val miniMessage = MiniMessage.miniMessage()

        lateinit var configManager: ConfigManager
            private set
        lateinit var registerManager: RegisterManager
            private set

        lateinit var economy: Economy
            private set
    }

    override fun onEnable() {

        logger.info("Plugin is being enabled...")

        //-> Custom
        instance = this

        //-> Configs
        configManager = ConfigManager(instance, dataFolder.toPath())
        loadLangFiles()
        loadConfigFiles()
        logger.info("Loaded ${configManager.getAllConfigs().size} configs!")

        //-> APIs
        if (!setupEconomy()) {
            logger.warning("Vault could not be found! Economy-Features won't work.")
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            CustomPlaceholders().register()
            logger.info("Hooked into PlaceholderAPI!")
        } else {
            logger.warning("PlaceholderAPI could not be found! Placeholders won't work.")
        }

        //-> Register
        registerManager = RegisterManager(instance)
        registerManager.register()

        logger.info("Plugin has been enabled!")

    }

    override fun onDisable() {

        logger.info("Plugin has been disabled!")

    }


    fun loadConfigFiles() {
        configManager.createConfig("config", "config.yml")
        configManager.createConfig("data", "data.yml")
        configManager.createConfig("custom-placeholders", "custom-placeholders.yml")

        loadLangFiles()
        val langFile = configManager.getConfig("config").getString("langFile", null)
        if (langFile == null) {
            logger.warning("No language file specified in config.yml! Defaulting to english.yml.")
            config.set("langFile", "english")
            configManager.saveConfig("config")
        }
        configManager.createConfig(langFile, "lang/$langFile.yml", "lang")
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
            configManager.createConfig(langConfig, "lang/$fileName", "lang")
        }

        Files.list(langDirectory).filter { it.toString().endsWith(".yml") }.forEach { langFile ->
            val langConfig = langFile.fileName.toString().removeSuffix(".yml")
            if (!configManager.existsConfig(langConfig)) {
                configManager.createConfig(langConfig, "lang/${langFile.fileName}", "lang")
            }
        }
    }

    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return false
        }

        val rsp = server.servicesManager.getRegistration<Economy?>(Economy::class.java)
        if (rsp == null) {
            return false
        }

        economy = rsp.getProvider()
        return economy != null
    }
}
