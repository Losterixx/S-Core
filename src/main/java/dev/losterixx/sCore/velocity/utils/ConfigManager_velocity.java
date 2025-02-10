package dev.losterixx.sCore.velocity.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import com.velocitypowered.api.plugin.PluginContainer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager_velocity {

    private final PluginContainer plugin;
    private final Path dataDirectory;
    private final Map<String, YamlDocument> configs = new HashMap<>();

    public ConfigManager_velocity(PluginContainer plugin, Path dataDirectory) {
        this.plugin = plugin;
        this.dataDirectory = dataDirectory;
        try {
            if (!Files.exists(dataDirectory)) Files.createDirectories(dataDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public YamlDocument createConfig(String name, String resourcePath) {
        Path configPath = dataDirectory.resolve(name + ".yml");
        File configFile = configPath.toFile();
        try {
            if (!configFile.exists()) {
                try (InputStream in = getResourceAsStream(resourcePath)) {
                    if (in == null) throw new IOException("Resource not found: " + resourcePath);
                    Files.copy(in, configPath);
                }
            }
            YamlDocument document = YamlDocument.create(configFile,
                    GeneralSettings.builder().build(),
                    LoaderSettings.builder().build());
            configs.put(name, document);
            return document;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream getResourceAsStream(String resourcePath) {
        return ConfigManager_velocity.class.getClassLoader().getResourceAsStream(resourcePath);
    }

    public YamlDocument getConfig(String name) {
        return configs.get(name);
    }

    public void save(String name) {
        YamlDocument document = configs.get(name);
        if (document != null) {
            try {
                document.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void reload(String name) {
        YamlDocument document = configs.get(name);
        if (document != null) {
            try {
                document.reload();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void saveAllConfigs() {
        for (YamlDocument document : configs.values()) {
            try {
                document.save();
            } catch (IOException e) { throw new RuntimeException(e); }
        }
    }

    public void reloadAllConfigs() {
        for (YamlDocument document : configs.values()) {
            try {
                document.reload();
            } catch (IOException e) { throw new RuntimeException(e); }
        }
    }

    public Map<String, YamlDocument> getAllConfigs() {
        return configs;
    }
}
