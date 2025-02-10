package dev.losterixx.sCore.paper;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.paper.utils.ConfigManager;
import dev.losterixx.sCore.paper.commands.SCoreCommand;
import dev.losterixx.sCore.paper.features.autobroadcaster.BroadcastCommand;
import dev.losterixx.sCore.paper.features.autobroadcaster.BroadcastManager;
import dev.losterixx.sCore.paper.features.invsee.EnderseeCommand;
import dev.losterixx.sCore.paper.features.invsee.ExtraseeCommand;
import dev.losterixx.sCore.paper.features.invsee.InvseeCommand;
import dev.losterixx.sCore.paper.features.spawn.AutoSpawnTeleportListener;
import dev.losterixx.sCore.paper.features.spawn.SetSpawnCommand;
import dev.losterixx.sCore.paper.features.spawn.SpawnCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class PaperMain extends JavaPlugin {

    private static PaperMain instance;
    public static MiniMessage mm = MiniMessage.miniMessage();
    private ConfigManager configManager;

    @Override
    public void onEnable() {

        getLogger().info("Plugin is enabling...");

        getLogger().info("Detected Serversoftware: Paper");

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

        //-> Features
        new BroadcastManager().startBroadcasting();

        //-> Commands & Listeners
        registerCommandsAndListeners();

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
        getCommand("setspawn").setExecutor(new SetSpawnCommand());
        getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("broadcast").setExecutor(new BroadcastCommand());
        getCommand("broadcast").setTabCompleter(new BroadcastCommand());
        getCommand("invsee").setExecutor(new InvseeCommand());
        getCommand("invsee").setTabCompleter(new InvseeCommand());
        getCommand("endersee").setExecutor(new EnderseeCommand());
        getCommand("endersee").setTabCompleter(new EnderseeCommand());
        getCommand("extrasee").setExecutor(new ExtraseeCommand());
        getCommand("extrasee").setTabCompleter(new ExtraseeCommand());

        //-> Listeners
        Bukkit.getPluginManager().registerEvents(new AutoSpawnTeleportListener(), instance);
        Bukkit.getPluginManager().registerEvents(new InvseeCommand(), instance);
        Bukkit.getPluginManager().registerEvents(new EnderseeCommand(), instance);
        Bukkit.getPluginManager().registerEvents(new ExtraseeCommand(), instance);

    }

    public static PaperMain getInstance() {
        return instance;
    }
    public ConfigManager getConfigManager() {
        return configManager;
    }
}
