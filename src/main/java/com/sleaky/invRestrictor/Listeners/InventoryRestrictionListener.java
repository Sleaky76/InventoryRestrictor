package com.sleaky.invRestrictor.Listeners;

import com.sleaky.invRestrictor.Logic.InventoryUtils;
import com.sleaky.invRestrictor.Logic.PlayerRestrictions;
import com.sleaky.invRestrictor.Logic.Restrictions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Stops players from putting items into slots that have been restricted,
 * and cleans up anything that ends up there through indirect means
 * (item pickups, shift-clicking from another inventory, etc.).
 */
public class InventoryRestrictionListener implements Listener {

    private final JavaPlugin plugin;

    public InventoryRestrictionListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Restrictions restrictions = PlayerRestrictions.getRestrictionsForPlayer(player);

        if (event.getClickedInventory() instanceof PlayerInventory) {
            int slot = event.getSlot();
            if (isValidSlot(slot) && restrictions.isRestrictedAt(slot)) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getClick() == ClickType.NUMBER_KEY) {
            int hotbarSlot = event.getHotbarButton();
            if (isValidSlot(hotbarSlot) && restrictions.isRestrictedAt(hotbarSlot)) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            Bukkit.getScheduler().runTask(plugin, () -> InventoryUtils.enforceRestrictions(player));
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Restrictions restrictions = PlayerRestrictions.getRestrictionsForPlayer(player);
        int topSize = event.getView().getTopInventory().getSize();

        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot < topSize) continue; // part of the open top inventory, not the player's

            int slot = event.getView().convertSlot(rawSlot);
            if (isValidSlot(slot) && restrictions.isRestrictedAt(slot)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        Restrictions restrictions = PlayerRestrictions.getRestrictionsForPlayer(player);

        int heldSlot = player.getInventory().getHeldItemSlot();
        if (restrictions.isRestrictedAt(40) || restrictions.isRestrictedAt(heldSlot)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Bukkit.getScheduler().runTask(plugin, () -> InventoryUtils.enforceRestrictions(player));
    }

    private boolean isValidSlot(int slot) {
        return slot >= 0 && slot <= 40;
    }
}