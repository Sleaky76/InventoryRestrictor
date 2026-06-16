package com.sleaky.invRestrictor.api;

import org.bukkit.entity.Player;

/**
 * Public api interface for plugin
 */
public interface InvRestrictorApi {

    void restrictSlot(Player player, int slot);

    void unrestrictSlot(Player player, int slot);

    void toggleSlot(Player player, int slot);

    boolean isRestricted(Player player, int slot);

    void restrictAllInventory(Player player);

    void unrestrictAllInventory(Player player);
}
