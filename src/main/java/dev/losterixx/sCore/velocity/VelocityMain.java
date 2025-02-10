package dev.losterixx.sCore.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sCore.general.GeneralMain;
import dev.losterixx.sCore.velocity.commands.*;
import dev.losterixx.sCore.velocity.utils.ConfigManager_velocity;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Arrays;

@Plugin(id = "score", name = "SPlaytime", version = GeneralMain.PLUGIN_VERSION, description = "-/-", authors = {"Losterixx"})
public class VelocityMain {

    @Inject
    private Logger logger;
    @Inject
    private ProxyServer proxy;
    @Inject
    @DataDirectory
    private Path dataDirectory;

    public static MiniMessage mm = MiniMessage.miniMessage();
    private static VelocityMain instance;
    private ConfigManager_velocity configManager;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        logger.info("V-S-Core is loading...");
        logger.info("Detected Serversoftware: Velocity");

        instance = this;

        PluginContainer container = proxy.getPluginManager().getPlugin("score")
                .orElseThrow(() -> new IllegalStateException("Plugin container not found"));
        configManager = new ConfigManager_velocity(container, dataDirectory);

        YamlDocument config = configManager.createConfig("velocity-config", "velocity-config.yml");
        YamlDocument messages = configManager.createConfig("velocity-messages", "velocity-messages.yml");

        logger.info("Detected " + proxy.getAllServers().size() + " servers!");

        registerCommandsAndListeners();

        logger.info("V-S-Core has been enabled!");

    }

    private void registerCommandsAndListeners() {
        //-> Listeners
        EventManager eventManager = proxy.getEventManager();
        //eventManager.register(this, new SampleListener());

        //-> Commands
        CommandManager commandManager = proxy.getCommandManager();
        CommandMeta vscoreCommandMeta = commandManager.metaBuilder("vscore")
                .aliases("vsc")
                .build();
        commandManager.register(vscoreCommandMeta, new SCoreCommand_velocity());

        String[] lobbyCommandAliases = new String[0];
        for (String alias : getConfigManager().getConfig("velocity-config").getStringList("lobbyCommand.aliases")) {
            lobbyCommandAliases = Arrays.copyOf(lobbyCommandAliases, lobbyCommandAliases.length + 1);
            lobbyCommandAliases[lobbyCommandAliases.length - 1] = alias;
        }

        CommandMeta lobbyCommandMeta = commandManager.metaBuilder("lobby")
                .aliases(lobbyCommandAliases)
                .build();
        commandManager.register(lobbyCommandMeta, new LobbyCommand_velocity());

        CommandMeta alertCommandMeta = commandManager.metaBuilder("alert")
                .build();
        commandManager.register(alertCommandMeta, new AlertCommand_velocity());

        CommandMeta findCommandMeta = commandManager.metaBuilder("find")
                .build();
        commandManager.register(findCommandMeta, new FindCommand_velocity());

        CommandMeta sendCommandMeta = commandManager.metaBuilder("send")
                .build();
        commandManager.register(sendCommandMeta, new SendCommand_velocity());
    }


    public static VelocityMain getInstance() { return instance; }
    public ConfigManager_velocity getConfigManager() { return configManager; }
    public ProxyServer getProxy() { return proxy; }

}
