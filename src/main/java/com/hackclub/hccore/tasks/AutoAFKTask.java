package com.hackclub.hccore.tasks;

import com.hackclub.hccore.HCCorePlugin;
import com.hackclub.hccore.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoAFKTask extends BukkitRunnable {
    private final HCCorePlugin plugin;

    public AutoAFKTask(HCCorePlugin plugin) {
        this.plugin = plugin;
    }

    public void run() {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            PlayerData data = this.plugin.getDataManager().getData(player);

            // Skip players who are already AFK
            if (data.isAfk()) {
                continue;
            }

            long secondsSinceLastActive =
                (System.currentTimeMillis() - data.getLastActiveAt()) / 1000;
            if (
                secondsSinceLastActive >
                this.plugin.getConfig().getInt("settings.auto-afk-time")
            ) {
                data.setAfk(true);
                player.sendMessage(
                    ChatColor.GRAY +
                    ChatColor.ITALIC.toString() +
                    "You’ve been automatically set to AFK due to inactivity."
                );
            }
        }
    }
}
