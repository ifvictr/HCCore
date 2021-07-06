package com.hackclub.hccore.commands;

import com.hackclub.hccore.HCCorePlugin;
import com.hackclub.hccore.PlayerData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class PingCommand implements TabExecutor {
    private final HCCorePlugin plugin;

    public PingCommand(HCCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
        CommandSender sender,
        Command cmd,
        String alias,
        String[] args
    ) {
        // /ping
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(
                    ChatColor.RED + "You must be a player to use this"
                );
                return true;
            }

            Player player = (Player) sender;
            int ping = this.getPing(player);
            // Failed for some reason
            if (ping == -1) {
                sender.sendMessage(ChatColor.RED + "Failed to get your ping");
                return true;
            }

            sender.sendMessage("Your ping is " + ping + "ms");
            return true;
        }

        // /ping [player]
        Player targetPlayer = sender.getServer().getPlayerExact(args[0]);
        if (targetPlayer != null) {
            PlayerData data =
                this.plugin.getDataManager().getData(targetPlayer);
            int ping = this.getPing(targetPlayer);
            // Failed for some reason
            if (ping == -1) {
                sender.sendMessage(
                    ChatColor.RED +
                    "Failed to get " +
                    data.getUsableName() +
                    "’s ping"
                );
                return true;
            }

            sender.sendMessage(
                data.getUsableName() + "’s ping is " + ping + "ms"
            );
        } else {
            sender.sendMessage(
                ChatColor.RED + "No online player with that name was found"
            );
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(
        CommandSender sender,
        Command cmd,
        String alias,
        String[] args
    ) {
        List<String> completions = new ArrayList<String>();
        if (args.length == 1) {
            for (Player player : sender.getServer().getOnlinePlayers()) {
                if (
                    StringUtil.startsWithIgnoreCase(player.getName(), args[0])
                ) {
                    completions.add(player.getName());
                }
            }
        }

        Collections.sort(completions);
        return completions;
    }

    private int getPing(Player player) {
        int ping = -1;
        try {
            // Use reflection because there's no Player#getPing method
            Object entityPlayer = player
                .getClass()
                .getMethod("getHandle")
                .invoke(player);
            ping =
                (int) entityPlayer
                    .getClass()
                    .getField("ping")
                    .get(entityPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ping;
    }
}
