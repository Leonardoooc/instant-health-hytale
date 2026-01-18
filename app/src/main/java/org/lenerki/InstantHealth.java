package org.lenerki;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import org.jline.utils.Log;
import org.lenerki.events.onPlaceBlock;
import org.lenerki.events.onRightClick;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

import javax.annotation.Nonnull;

public class InstantHealth extends JavaPlugin {
  public InstantHealth(JavaPluginInit init) {
    super(init);
  }

  @Override
  public void setup() {
    loadConfig(getDataDirectory());
    Config.init(configPath.toFile());
    getLogger().at(Level.INFO).log("Instant Health is loading");
  }

  @Override
  public void start() {
    registerEvents();
    getLogger().at(Level.INFO).log("Instant Health has started!");
  }

  @Override
  public void shutdown() {
    getLogger().at(Level.INFO).log("Instant Health unloaded");
  }

  private Path configPath;
  public void loadConfig(@Nonnull Path dataFolder) {
    this.configPath = dataFolder.resolve("config.toml");

    if (!Files.exists(this.configPath)) {
      try {
        Path parentDir = this.configPath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
          Files.createDirectories(parentDir);
        }

        InputStream is = getClass().getClassLoader().getResourceAsStream("config.toml");
        if (is != null) {
          try {
            Files.copy(is, this.configPath);
            Log.info("Created default config at: " + this.configPath.toAbsolutePath());
          } finally {
            is.close();
          }
        } else {
          Log.error("Could not find config.toml in resources.");
        }
      } catch (IOException e) {
        Log.error("Failed to create default config: " + e.getMessage());
        e.printStackTrace();
      }
    } else {
      Log.info("Config already exists at: " + this.configPath.toAbsolutePath());
    }
  }

  public void registerEvents() {
    new onRightClick().register();
    new onPlaceBlock(this).register();
  }
}
