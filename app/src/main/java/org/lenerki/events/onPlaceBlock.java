package org.lenerki.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import org.jline.utils.Log;
import org.lenerki.Config;

public class onPlaceBlock {
  private final JavaPlugin plugin;

  public onPlaceBlock(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  public void register() {
    plugin.getEventRegistry().registerGlobal(PlaceBlockEvent.class, this::onPlaceBlockEvent);
  }

  private void onPlaceBlockEvent(PlaceBlockEvent event) {
    ItemStack itemInHand = event.getItemInHand();
    if (itemInHand != null && Config.getInstance().isHealItem(itemInHand.getItemId())) {
      event.setCancelled(true);
    }
  }
}
