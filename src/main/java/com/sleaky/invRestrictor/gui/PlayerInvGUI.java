package com.sleaky.invRestrictor.gui;

import com.sleaky.invRestrictor.utils.InventoryUtils;
import com.sleaky.invRestrictor.logic.PlayerRestrictions;
import com.sleaky.invRestrictor.logic.Restrictions;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Shows which of a player's inventory slots are restricted, and lets
 * the viewer click a pane to toggle that restriction on or off.
 */
public class PlayerInvGUI implements InventoryHolder {

    private final Inventory inventory;
    private final Player victim;
    private final NamespacedKey key;

    public PlayerInvGUI(Player victim, JavaPlugin plugin) {
        Objects.requireNonNull(victim, "Player is null cannot proceed");
        this.victim = victim;
        this.key = new NamespacedKey(plugin, "InvUniqueString");
        this.inventory = Bukkit.createInventory(this, 54, Component.text(victim.getName() + "'s restrictions"));
        setUpInv();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public Player getVictim() {
        return victim;
    }

    public void openGUIFor(Player p) {
        p.openInventory(inventory);
    }

    /**
     * Toggles the restriction for the player-inventory slot tied to the
     * clicked GUI pane, refreshes that pane, and evicts any item that's
     * now sitting in a newly-restricted slot.
     */
    public void toggleSlot(int guiSlot) {
        ItemStack clicked = inventory.getItem(guiSlot);
        if (clicked == null || !clicked.hasItemMeta()) return;

        ItemMeta meta = clicked.getItemMeta();
        Byte slotIndex = meta.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
        if (slotIndex == null) return;

        Restrictions restrictions = PlayerRestrictions.getRestrictionsForPlayer(victim);
        restrictions.toggle(slotIndex);
        boolean restricted = restrictions.isRestrictedAt(slotIndex);

        refreshPane(guiSlot, slotIndex, restricted);

        InventoryUtils.enforceRestrictions(victim);
    }

    private void setUpInv() {
        Restrictions restrictions = PlayerRestrictions.getRestrictionsForPlayer(victim);

        for (int i = 0; i < 36; i++) {
            refreshPane(i, i, restrictions.isRestrictedAt(i));
        }
        for (int i = 45; i < 49; i++) {
            int playerSlot = i - 9;
            refreshPane(i, playerSlot, restrictions.isRestrictedAt(playerSlot));
        }
        refreshPane(53, 40, restrictions.isRestrictedAt(40));
    }

    private void refreshPane(int guiSlot, int playerSlot, boolean restricted) {
        ItemStack pane = new ItemStack(restricted ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = pane.getItemMeta();
        meta.displayName(Component.text(labelFor(guiSlot) + (restricted ? " - Restricted" : "")));
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) playerSlot);
        pane.setItemMeta(meta);
        inventory.setItem(guiSlot, pane);
    }

    private String labelFor(int guiSlot) {
        if (guiSlot < 36) return "Inventory Slot";
        if (guiSlot >= 45 && guiSlot < 49) return "Armor Slot";
        return "OffHand Slot";
    }
}