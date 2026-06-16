package com.sleaky.invRestrictor;

import com.sleaky.invRestrictor.commands.RestrictCommand;
import com.sleaky.invRestrictor.listeners.BarrierItemListener;
import com.sleaky.invRestrictor.listeners.GUIListener;
import com.sleaky.invRestrictor.listeners.InventoryRestrictionListener;
import com.sleaky.invRestrictor.listeners.Joins;
import com.sleaky.invRestrictor.utils.InventoryUtils;
import com.sleaky.invRestrictor.logic.PlayerRestrictions;
import com.sleaky.invRestrictor.logic.Restrictions;
import com.sleaky.invRestrictor.api.InvRestrictorApi;
import com.sleaky.invRestrictor.api.InvRestrictorProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class InvRestrictor extends JavaPlugin implements InvRestrictorApi {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Joins(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryRestrictionListener(this), this);
        getServer().getPluginManager().registerEvents(new BarrierItemListener(this), this);

        Objects.requireNonNull(getCommand("restrict")).setExecutor(new RestrictCommand());

        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerRestrictions.addNewPlayer(p);
        }

        InvRestrictorProvider.setApi(this);

        getServer().getServicesManager().register(
                InvRestrictorApi.class, this, this, ServicePriority.Normal
        );
    }

    @Override
    public void onDisable() {
        InvRestrictorProvider.setApi(null);
    }

    @Override
    public void restrictSlot(Player player, int slot) {
        PlayerRestrictions.getRestrictionsForPlayer(player).setRestricted(slot);
        InventoryUtils.enforceRestrictions(player);
    }

    @Override
    public void unrestrictSlot(Player player, int slot) {
        PlayerRestrictions.getRestrictionsForPlayer(player).setUnrestricted(slot);
        InventoryUtils.enforceRestrictions(player);
    }

    @Override
    public void toggleSlot(Player player, int slot) {
        PlayerRestrictions.getRestrictionsForPlayer(player).toggle(slot);
        InventoryUtils.enforceRestrictions(player);
    }

    @Override
    public boolean isRestricted(Player player, int slot) {
        return PlayerRestrictions.getRestrictionsForPlayer(player).isRestrictedAt(slot);
    }

    @Override
    public void restrictAllInventory(Player player) {
        Restrictions r = PlayerRestrictions.getRestrictionsForPlayer(player);
        for (int i = 0; i < 36; i++) r.setRestricted(i);
        InventoryUtils.enforceRestrictions(player);
    }

    @Override
    public void unrestrictAllInventory(Player player) {
        Restrictions r = PlayerRestrictions.getRestrictionsForPlayer(player);
        for (int i = 0; i < 36; i++) r.setUnrestricted(i);
        InventoryUtils.enforceRestrictions(player);
    }
}