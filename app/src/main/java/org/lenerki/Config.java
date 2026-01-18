package org.lenerki;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
  private static Config instance;
  private Map<String, Double> healItems;

  private Config(File configFile) {
    healItems = new HashMap<>();
    try {
      Toml toml = new Toml().read(configFile);
      List<Toml> items = toml.getTables("healItem");

      if (items != null && !items.isEmpty()) {
        for (Toml item : items) {
          String itemId = item.getString("itemId");
          Double healAmount = item.getDouble("healAmount");
          if (itemId != null && healAmount != null) {
            healItems.put(itemId.toLowerCase(), healAmount);
          }
        }
      }
      
      // Valores padrão caso não haja configuração
      if (healItems.isEmpty()) {
        healItems.put("potion_health", 20.0);
      }
    } catch (Exception e) {
      healItems.put("potion_health", 20.0);
    }
  }

  public static void init(File configFile) {
      instance = new Config(configFile);
  }

  public static Config getInstance() {
    if (instance == null) {
      throw new IllegalStateException("Config not initialized! Call Config.init() first.");
    }
    return instance;
  }

  public Map<String, Double> getHealItems() {
    return healItems;
  }
  
  public boolean isHealItem(String itemId) {
    return healItems.containsKey(itemId.toLowerCase());
  }
  
  public double getHealAmount(String itemId) {
    return healItems.getOrDefault(itemId.toLowerCase(), 0.0);
  }
}
