package dev.losterixx.sCore.paper.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final JavaPlugin plugin;
    private final Path dataDirectory;
    private final Map<String, YamlDocument> configs = new HashMap<>();

    public ConfigManager(JavaPlugin plugin, Path dataDirectory) {
        this.plugin = plugin;
        this.dataDirectory = dataDirectory.toAbsolutePath();
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
                try (InputStream in = plugin.getResource(resourcePath)) {
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

    public YamlDocument getConfig(String name) {
        return configs.get(name);
    }

    public void saveConfig(String name) {
        YamlDocument document = configs.get(name);
        if (document != null) {
            try {
                document.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void reloadConfig(String name) {
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