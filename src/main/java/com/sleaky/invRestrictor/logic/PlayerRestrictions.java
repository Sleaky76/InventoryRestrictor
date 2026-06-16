package com.sleaky.invRestrictor.logic;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Keeps track of every online player's slot restrictions in memory.
 */
public class PlayerRestrictions {

    private static final Map<UUID, Restrictions> restrictionsMap = new HashMap<>();

    public static void addNewPlayer(Player p) {
        restrictionsMap.putIfAbsent(p.getUniqueId(), new Restrictions());
    }

    public static void removePlayer(Player p) {
        restrictionsMap.remove(p.getUniqueId());
    }

    public static Restrictions getRestrictionsForPlayer(Player p) {
        return restrictionsMap.computeIfAbsent(p.getUniqueId(), id -> new Restrictions());
    }
}