package com.sleaky.invRestrictor.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import com.sleaky.invRestrictor.logic.Restrictions;
import com.sleaky.invRestrictor.logic.PlayerRestrictions;

import java.util.List;

/**
 * Keeps a player's actual inventory consistent with their restrictions:
 * restricted slots are filled with a "Restriction Barrier" placeholder
 * item, and any real item that ends up there gets moved elsewhere.
 */
public final class InventoryUtils {

    private InventoryUtils() {}

    public static final int MAIN_INVENTORY_SIZE = 36;
    public static final int TOTAL_SLOTS = 41;

    /** Marker key used to identify restriction-barrier placeholder items. */
    public static final NamespacedKey BARRIER_MARKER = new NamespacedKey("invrestrictor", "restriction_barrier");

    /**
     * Builds the barrier item placed in restricted slots.
     */
    public static ItemStack createBarrierItem() {
        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta meta = barrier.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Restricted Slot")
                    .color(NamedTextColor.RED)
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                    Component.text("You cannot use this slot.")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
            meta.getPersistentDataContainer().set(BARRIER_MARKER, PersistentDataType.BYTE, (byte) 1);
            barrier.setItemMeta(meta);
        }
        return barrier;
    }

    /**
     * Returns true if the given item is a restriction-barrier placeholder.
     */
    public static boolean isRestrictionBarrier(ItemStack item) {
        if (item == null || item.getType() != Material.BARRIER || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(BARRIER_MARKER, PersistentDataType.BYTE);
    }

    /**
     * Synchronises the player's inventory with their current restrictions:
     * - Restricted slots that don't have a barrier get any real item moved
     *   out (or dropped if there's no room) and a barrier placed in.
     * - Unrestricted slots that have a barrier have it removed.
     */
    public static void enforceRestrictions(Player player) {
        Restrictions restrictions = PlayerRestrictions.getRestrictionsForPlayer(player);
        PlayerInventory inv = player.getInventory();

        for (int slot = 0; slot < TOTAL_SLOTS; slot++) {
            ItemStack item = inv.getItem(slot);
            boolean isBarrier = isRestrictionBarrier(item);

            if (restrictions.isRestrictedAt(slot)) {
                if (isBarrier) continue;

                if (item != null && item.getType() != Material.AIR) {
                    inv.setItem(slot, null);
                    ItemStack leftover = placeInUnrestrictedSlot(inv, restrictions, item);
                    if (leftover != null) {
                        player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                    }
                }
                inv.setItem(slot, createBarrierItem());
            } else if (isBarrier) {
                inv.setItem(slot, null);
            }
        }
    }

    private static ItemStack placeInUnrestrictedSlot(PlayerInventory inv, Restrictions restrictions, ItemStack item) {
        for (int slot = 0; slot < MAIN_INVENTORY_SIZE; slot++) {
            if (restrictions.isRestrictedAt(slot)) continue;

            ItemStack existing = inv.getItem(slot);
            if (existing != null && existing.isSimilar(item) && existing.getAmount() < existing.getMaxStackSize()) {
                int space = existing.getMaxStackSize() - existing.getAmount();
                int move = Math.min(space, item.getAmount());
                existing.setAmount(existing.getAmount() + move);
                item.setAmount(item.getAmount() - move);
                if (item.getAmount() <= 0) return null;
            }
        }

        for (int slot = 0; slot < MAIN_INVENTORY_SIZE; slot++) {
            if (restrictions.isRestrictedAt(slot)) continue;

            ItemStack existing = inv.getItem(slot);
            if (existing == null || existing.getType() == Material.AIR) {
                inv.setItem(slot, item);
                return null;
            }
        }

        return item;
    }
}