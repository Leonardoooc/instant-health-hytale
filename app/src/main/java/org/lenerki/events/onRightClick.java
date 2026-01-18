package org.lenerki.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.DamageCause;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.lenerki.Config;

public class onRightClick {
  private static Map<UUID, Long> usedSoup = new ConcurrentHashMap<>();

  public static void register() {
    PacketAdapters.registerInbound((PlayerPacketFilter) onRightClick::onPacketReceived);
  }

  private static boolean onPacketReceived(PlayerRef player, Packet packet) {
    if (packet.getId() == 290 && packet instanceof SyncInteractionChains interactionChains) {
      SyncInteractionChain[] updates = interactionChains.updates;
      for (SyncInteractionChain chain : updates) {
        if (chain.interactionType != null && chain.interactionType.name().equals("Secondary")) {
          EntityStore entityStore = (EntityStore) player.getReference().getStore().getExternalData();
          World world = entityStore.getWorld();
          world.execute(() -> onUseSoup(player));
        }
      }
    }

    return false;
  }

  public static boolean isSoupInHand(PlayerRef player) {
    Ref<EntityStore> playerRef = player.getReference();
    Store<EntityStore> store = playerRef.getStore();
    Player playerEntity = store.getComponent(playerRef, Player.getComponentType());
    ItemStack itemInHand = playerEntity.getInventory().getItemInHand();

    return itemInHand != null && Config.getInstance().isHealItem(itemInHand.getItemId());
  }

  public static boolean canUseSoup(PlayerRef player) {
    Long lastUsed = usedSoup.get(player.getUuid());
    if (lastUsed == null) {
      return true;
    }

    long currentTime = System.currentTimeMillis();
    return (currentTime - lastUsed) >= 250;
  }

  public static void onUseSoup(PlayerRef player) {
    if (!canUseSoup(player)) {
      return;
    }

    if (!isSoupInHand(player)) {
      return;
    }

    Store<EntityStore> store = player.getReference().getStore();
    Player playerEntity = store.getComponent(player.getReference(), Player.getComponentType());
    ItemStack itemInHand = playerEntity.getInventory().getItemInHand();

    if (itemInHand != null) {
      float healAmount = -(float) Config.getInstance().getHealAmount(itemInHand.getItemId());
      Damage damage = new Damage(Damage.NULL_SOURCE, DamageCause.NULLABLE_BIT_FIELD_SIZE, healAmount);
      DamageSystems.executeDamage(player.getReference(), store, damage);

      usedSoup.put(player.getUuid(), System.currentTimeMillis());

      int currentQuantity = itemInHand.getQuantity();
      if (currentQuantity > 1) {
        ItemStack newStack = new ItemStack(itemInHand.getItemId(), currentQuantity - 1);
        byte activeSlot = playerEntity.getInventory().getActiveHotbarSlot();
        playerEntity.getInventory().getHotbar().setItemStackForSlot(activeSlot, newStack);
      } else {
        byte activeSlot = playerEntity.getInventory().getActiveHotbarSlot();
        playerEntity.getInventory().getHotbar().removeItemStackFromSlot(activeSlot);
      }
    }
  }
}
