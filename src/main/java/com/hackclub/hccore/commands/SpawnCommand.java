package com.hackclub.hccore.commands;

import com.hackclub.hccore.HCCorePlugin;
import com.hackclub.hccore.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {
    private final HCCorePlugin plugin;

    public SpawnCommand(HCCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this");
            return true;
        }

        Player player = (Player) sender;

        // Player needs to be in the Overworld
        if (player.getWorld().getEnvironment() != Environment.NORMAL) {
            sender.sendMessage(ChatColor.RED + "You can only use this command in the Overworld");
            return true;
        }

        // Player needs to be within the allowed radius from spawn
        int distanceFromSpawn =
                (int) player.getLocation().distance(player.getWorld().getSpawnLocation());
        int allowedRadius =
                this.plugin.getConfig().getInt("settings.spawn-command.allowed-radius");
        if (distanceFromSpawn > allowedRadius) {
            sender.sendMessage(ChatColor.RED + "You need to be within " + allowedRadius
                    + " blocks from spawn to use this command. Currently, you’re "
                    + (distanceFromSpawn - allowedRadius) + " blocks too far.");
            return true;
        }

        // Player needs to be on the ground
        if (!player.isOnGround()) {
            sender.sendMessage(
                    ChatColor.RED + "You need to be standing on the ground to use this command");
            return true;
        }

        // Player needs to have sky access (no blocks above them at all)
        Block highestBlock = player.getWorld().getHighestBlockAt(player.getLocation());
        if (player.getLocation().getY() < highestBlock.getY()) {
            sender.sendMessage(
                    ChatColor.RED + "You need be directly under the sky to use this command");
            return true;
        }

        // Can't be used while in damage cooldown period
        PlayerData data = this.plugin.getDataManager().getData(player);
        long currentTime = System.currentTimeMillis();
        long secondsSinceLastDamaged = (currentTime - data.getLastDamagedAt()) / 1000;
        int damageCooldown =
                this.plugin.getConfig().getInt("settings.spawn-command.damage-cooldown");
        if (secondsSinceLastDamaged < damageCooldown) {
            sender.sendMessage(
                    ChatColor.RED + "You were hurt recently—you’ll be able to use this command in "
                            + (damageCooldown - secondsSinceLastDamaged) + " seconds");
            return true;
        }

        player.teleport(player.getWorld().getSpawnLocation());
        sender.sendMessage(ChatColor.GREEN + "You’ve been teleported to the world spawn");

        return true;
    }
}
