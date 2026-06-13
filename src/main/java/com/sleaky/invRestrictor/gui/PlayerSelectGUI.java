package com.sleaky.invRestrictor.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class PlayerSelectGUI implements InventoryHolder {

    private final Inventory selectionScreen;

    public PlayerSelectGUI(Player p) {
        selectionScreen = Bukkit.createInventory(this, 27, Component.text("Select a player"));
    }

    @Override
    public @NotNull Inventory getInventory() {
        return selectionScreen;
    }

    public void openGUIFor(Player p) {
        setupInv();
        p.openInventory(selectionScreen);
    }

    private void setupInv() {
        int i = 0;
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (i >= selectionScreen.getSize()) break;

            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(target);
                meta.displayName(Component.text(target.getName()));
                playerHead.setItemMeta(meta);
            }

            selectionScreen.setItem(i, playerHead);
            i++;
        }
    }
}