package com.mclawa.playerdb.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PDBEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
