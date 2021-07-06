package com.hackclub.hccore.listeners;

import com.hackclub.hccore.HCCorePlugin;
import com.hackclub.hccore.PlayerData;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerListener implements Listener {
    private final HCCorePlugin plugin;

    public PlayerListener(HCCorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        this.plugin.getDataManager()
            .getData(player)
            .setLastDamagedAt(System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        String message = event.getDeathMessage();
        message =
            message.replace(
                event.getEntity().getName(),
                ChatColor.stripColor(event.getEntity().getDisplayName())
            );

        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            message =
                message.replace(
                    killer.getName(),
                    ChatColor.stripColor(killer.getDisplayName())
                );
        }

        event.setDeathMessage(message);
    }

    @EventHandler
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        // Apply the player's chat color to the message and translate color codes

        PlayerData data =
            this.plugin.getDataManager().getData(event.getPlayer());
        net.md_5.bungee.api.ChatColor messageColor = data
            .getMessageColor()
            .asBungee();
        net.md_5.bungee.api.ChatColor nameColor = data
            .getNameColor()
            .asBungee();

        TextComponent nameComponent = new TextComponent(
            event.getPlayer().getDisplayName()
        );
        nameComponent.setColor(nameColor);
        nameComponent.setHoverEvent(
            new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(event.getPlayer().getName()).create()
            )
        );

        TextComponent arrowComponent = new TextComponent(" » ");
        arrowComponent.setColor(ChatColor.GOLD.asBungee());

        TextComponent playerChatComponent = new TextComponent(
            ChatColor.translateAlternateColorCodes('&', event.getMessage())
        );
        playerChatComponent.setColor(messageColor);

        this.plugin.getServer()
            .spigot()
            .broadcast(nameComponent, arrowComponent, playerChatComponent);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST) // Runs foremost
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.plugin.getDataManager().registerPlayer(event.getPlayer());
        // Set the initial active time
        this.plugin.getDataManager()
            .getData(event.getPlayer())
            .setLastActiveAt(System.currentTimeMillis());

        // NOTE: Title isn't cleared when the player leaves the server
        event.getPlayer().resetTitle();
        event.setJoinMessage(
            ChatColor.YELLOW +
            ChatColor.stripColor(event.getPlayer().getDisplayName()) +
            " joined the game"
        );
    }

    @EventHandler
    public void onPlayerLogin(final PlayerLoginEvent event) {
        if (
            event.getResult() == PlayerLoginEvent.Result.ALLOWED ||
            event.getResult() == PlayerLoginEvent.Result.KICK_OTHER
        ) {
            return;
        }

        String message = null;
        switch (event.getResult()) {
            case KICK_BANNED:
                message =
                    ChatColor.RED +
                    ChatColor.BOLD.toString() +
                    "You’ve been banned :(\n\n" +
                    ChatColor.RESET +
                    ChatColor.WHITE +
                    "If you believe this was a mistake, please DM " +
                    ChatColor.AQUA +
                    "@zane " +
                    ChatColor.WHITE +
                    "on Slack.";
                break;
            case KICK_FULL:
                message =
                    ChatColor.RED +
                    ChatColor.BOLD.toString() +
                    "The server is full!\n\n" +
                    ChatColor.RESET +
                    ChatColor.WHITE +
                    "Sorry, it looks like there’s no more room. Please try again in ~20 minutes.";
                break;
            case KICK_WHITELIST:
                message =
                    ChatColor.RED +
                    ChatColor.BOLD.toString() +
                    "You’re not whitelisted!\n\n" +
                    ChatColor.RESET +
                    ChatColor.WHITE +
                    "Join " +
                    ChatColor.AQUA +
                    "#minecraft " +
                    ChatColor.WHITE +
                    "on Slack and ping " +
                    ChatColor.AQUA +
                    "@zane " +
                    ChatColor.WHITE +
                    "to be added.";
                break;
            default:
                break;
        }
        event.setKickMessage(message);
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        // Reset lastDamagedAt
        this.plugin.getDataManager()
            .getData(event.getPlayer())
            .setLastDamagedAt(0);
    }

    @EventHandler(priority = EventPriority.MONITOR) // Runs very last
    public void onPlayerQuit(final PlayerQuitEvent event) {
        // NOTE: Title isn't cleared when the player leaves the server
        // event.getPlayer().resetTitle();
        event.setQuitMessage(
            ChatColor.YELLOW +
            ChatColor.stripColor(event.getPlayer().getDisplayName()) +
            " left the game"
        );

        this.plugin.getDataManager().unregisterPlayer(event.getPlayer());
    }
}
