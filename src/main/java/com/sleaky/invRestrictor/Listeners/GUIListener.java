package com.sleaky.invRestrictor.Listeners;

import com.sleaky.invRestrictor.gui.PlayerInvGUI;
import com.sleaky.invRestrictor.gui.PlayerSelectGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class GUIListener implements Listener {

    private final JavaPlugin plugin;

    public GUIListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player p)) return;

        InventoryHolder topHolder = event.getView().getTopInventory().getHolder();
        boolean clickedTop = event.getClickedInventory() == event.getView().getTopInventory();

        if (topHolder instanceof PlayerSelectGUI) {
            event.setCancelled(true);
            if (clickedTop) handleSelectClick(event, p);
            return;
        }

        if (topHolder instanceof PlayerInvGUI gui) {
            event.setCancelled(true);
            if (clickedTop) gui.toggleSlot(event.getSlot());
        }
    }

    private void handleSelectClick(InventoryClickEvent event, Player p) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !(clicked.getItemMeta() instanceof SkullMeta meta)) return;
        if (meta.getOwningPlayer() == null) return;

        Player victim = Bukkit.getPlayer(meta.getOwningPlayer().getUniqueId());
        if (victim == null) {
            p.sendMessage("That player is no longer online.");
            return;
        }

        PlayerInvGUI gui = new PlayerInvGUI(victim, plugin);
        gui.openGUIFor(p);
    }
}