package com.hackclub.hccore.listeners;

import com.hackclub.hccore.HCCorePlugin;
import com.hackclub.hccore.PlayerData;
import com.hackclub.hccore.events.player.PlayerAFKStatusChangeEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class AFKListener implements Listener {
    private final HCCorePlugin plugin;

    public AFKListener(HCCorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerAFKStatusChange(
        final PlayerAFKStatusChangeEvent event
    ) {
        Player player = event.getPlayer();
        PlayerData data = this.plugin.getDataManager().getData(player);

        if (event.getNewValue()) {
            player.sendTitle(
                ChatColor.RED + ChatColor.BOLD.toString() + "You are AFK",
                "Run /afk to mark yourself as active",
                10,
                999999,
                20
            );
            player
                .getServer()
                .broadcastMessage(data.getUsableName() + " is now AFK");
        } else {
            player.sendTitle(null, null, 0, 1, -1);
            player
                .getServer()
                .broadcastMessage(data.getUsableName() + " is now active");
        }
    }

    @EventHandler
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        this.plugin.getDataManager()
            .getData(event.getPlayer())
            .setLastActiveAt(System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerCommandPreprocess(
        final PlayerCommandPreprocessEvent event
    ) {
        this.plugin.getDataManager()
            .getData(event.getPlayer())
            .setLastActiveAt(System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        this.plugin.getDataManager()
            .getData(event.getPlayer())
            .setLastActiveAt(System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        // Make sure the player didn't just turn their head
        if (from.toVector().equals(to.toVector())) { // NOTE: Might be inefficient?
            return;
        }

        this.plugin.getDataManager()
            .getData(event.getPlayer())
            .setLastActiveAt(System.currentTimeMillis());
    }
}
