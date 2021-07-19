package com.hackclub.hccore.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerAFKStatusChangeEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    private final boolean isAfk;

    public PlayerAFKStatusChangeEvent(Player player, boolean isAfk) {
        super(player);
        this.player = player;
        this.isAfk = isAfk;
    }

    public boolean getNewValue() {
        return this.isAfk;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
