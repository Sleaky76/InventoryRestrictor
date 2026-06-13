package com.sleaky.invRestrictor;

import com.sleaky.invRestrictor.Commands.RestrictCommand;
import com.sleaky.invRestrictor.Listeners.BarrierItemListener;
import com.sleaky.invRestrictor.Listeners.GUIListener;
import com.sleaky.invRestrictor.Listeners.InventoryRestrictionListener;
import com.sleaky.invRestrictor.Listeners.Joins;
import com.sleaky.invRestrictor.Logic.PlayerRestrictions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class InvRestrictor extends JavaPlugin {

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
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}