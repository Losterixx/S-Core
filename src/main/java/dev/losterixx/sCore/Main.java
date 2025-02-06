package dev.losterixx.sCore;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.commands.SCoreCommand;
import dev.losterixx.sCore.utils.ConfigManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    public static MiniMessage mm = MiniMessage.miniMessage();
    private ConfigManager configManager;

    @Override
    public void onEnable() {

        getLogger().info("Plugin is enabling...");

        //-> Custom
        instance = this;

        //-> Configs
        configManager = new ConfigManager(this, getDataFolder().toPath());
        YamlDocument config = configManager.createConfig("config", "config.yml");
        YamlDocument messages = configManager.createConfig("messages", "messages.yml");
        YamlDocument data = configManager.createConfig("data", "data.yml");
        getLogger().info("Loaded " + configManager.getAllConfigs().size() + " configs!");

        //-> APIs
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            //new Placeholders().register();
            getLogger().info("Hooked into PlaceholderAPI!");
        } else {
            getLogger().warning("PlaceholderAPI could not be found! Placeholders won't work.");
        }

        getLogger().info("Plugin has been enabled!");

    }

    @Override
    public void onDisable() {

        getLogger().info("Plugin has been disabled!");

    }

    public void registerCommandsAndListeners() {
        HandlerList.unregisterAll(instance);

        //-> Commands
        getCommand("score").setExecutor(new SCoreCommand());
        getCommand("score").setTabCompleter(new SCoreCommand());

        //-> Listeners

    }

    public static Main getInstance() {
        return instance;
    }
    public ConfigManager getConfigManager() {
        return configManager;
    }
}
