package com.sleaky.invRestrictor.logic;

import java.util.ArrayList;
import java.util.List;

public class Restrictions {

    public static final int SIZE = 41; // 36 inventory + 4 armor + 1 offhand

    private final List<Boolean> restricted;

    protected Restrictions() {
        List<Boolean> init = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            init.add(false);
        }
        restricted = init;
    }

    public void setRestricted(int i) {
        restricted.set(i, true);
    }

    public void setUnrestricted(int i) {
        restricted.set(i, false);
    }

    public void toggle(int i) {
        restricted.set(i, !restricted.get(i));
    }

    public boolean isRestrictedAt(int i) {
        return restricted.get(i);
    }
}