package com.hackclub.hccore.tasks;

import com.hackclub.hccore.HCCorePlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.World.Environment;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CheckAdAstraTask extends BukkitRunnable {
    private static final int MIN_Y = 10000;

    private final HCCorePlugin plugin;

    public CheckAdAstraTask(HCCorePlugin plugin) {
        this.plugin = plugin;
    }

    public void run() {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            // Player needs to be in the Overworld
            if (player.getWorld().getEnvironment() != Environment.NORMAL) {
                continue;
            }

            // Player needs to be above the minimum y level
            if (player.getLocation().getY() < CheckAdAstraTask.MIN_Y) {
                continue;
            }

            NamespacedKey key = new NamespacedKey(this.plugin, "ad_astra");
            AdvancementProgress progress = player.getAdvancementProgress(
                player.getServer().getAdvancement(key)
            );
            // Skip if player already has this advancement
            if (progress.isDone()) {
                continue;
            }

            this.grantAdvancement(player, key);
        }
    }

    private void grantAdvancement(Player player, NamespacedKey key) {
        AdvancementProgress progress = player.getAdvancementProgress(
            player.getServer().getAdvancement(key)
        );
        if (progress.isDone()) {
            return;
        }

        for (String criteria : progress.getRemainingCriteria()) {
            progress.awardCriteria(criteria);
        }
    }
}
