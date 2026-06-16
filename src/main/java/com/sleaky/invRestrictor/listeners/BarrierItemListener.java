package com.sleaky.invRestrictor.listeners;

import com.sleaky.invRestrictor.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;

/**
 * Prevents the restriction-barrier placeholder item from being dropped,
 * placed as a block, or surviving a death drop, and re-applies barriers
 * after a respawn (since death clears the inventory).
 */
public class BarrierItemListener implements Listener {

    private final JavaPlugin plugin;

    public BarrierItemListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (InventoryUtils.isRestrictionBarrier(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack inHand = event.getItemInHand();
        if (InventoryUtils.isRestrictionBarrier(inHand)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Iterator<ItemStack> drops = event.getDrops().iterator();
        while (drops.hasNext()) {
            if (InventoryUtils.isRestrictionBarrier(drops.next())) {
                drops.remove();
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(plugin, () -> InventoryUtils.enforceRestrictions(player));
    }
}