package com.hackclub.hccore.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import com.hackclub.hccore.HCCorePlugin;
import com.hackclub.hccore.PlayerData;
import com.hackclub.hccore.utils.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class StatsCommand implements TabExecutor {
    private static final List<String> STATISTIC_NAMES = Arrays.asList(Statistic.values()).stream()
            .map(statistic -> statistic.name().toLowerCase()).collect(Collectors.toList());

    private final HCCorePlugin plugin;

    public StatsCommand(HCCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        boolean extended = false;

        // /stats
        if (args.length == 0) {
            if (sender instanceof Player) {
                sender.sendMessage("Your stats:");
                this.sendStatistics(sender, (Player) sender, extended);
            } else {
                sender.sendMessage(ChatColor.RED + "You must be a player to use this");
            }
            return true;
        }

        if (args.length > 1) {
            switch (args[1].toLowerCase()) {
                case "extended": // /stats <player> extended
                    extended = true;
                    break;
                case "only": // /stats <player> only <statistic>
                    if (args.length < 3) {
                        sender.sendMessage(ChatColor.RED
                                + "You must include both a player and statistic name");
                        return true;
                    }
                    if (!STATISTIC_NAMES.contains(args[2].toLowerCase())) {
                        sender.sendMessage(ChatColor.RED + "Not a valid statistic");
                        return true;
                    }
                    Statistic specificStat = Statistic.valueOf(args[2].toUpperCase());
                    if (specificStat.isSubstatistic()) {
                        sender.sendMessage(
                                ChatColor.RED + "This statistic is not currently supported");
                        return true;
                    }

                    Player player = sender.getServer().getPlayerExact(args[0]);
                    if (player != null) {
                        PlayerData data = this.plugin.getDataManager().getData(player);
                        sender.sendMessage(data.getUsableName() + "’s " + args[2].toLowerCase()
                                + " statistic: " + player.getStatistic(specificStat));
                    } else {
                        sender.sendMessage(
                                ChatColor.RED + "No online player with that name was found");
                    }
                    return true;
                default:
                    return false;
            }
        }

        // /stats <player>
        Player targetPlayer = sender.getServer().getPlayerExact(args[0]);
        if (targetPlayer != null) {
            PlayerData data = this.plugin.getDataManager().getData(targetPlayer);
            sender.sendMessage(data.getUsableName() + "’s stats:");
            this.sendStatistics(sender, targetPlayer, extended);
        } else {
            sender.sendMessage(ChatColor.RED + "No online player with that name was found");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias,
            String[] args) {
        List<String> completions = new ArrayList<String>();

        switch (args.length) {
            case 1:
                for (Player player : sender.getServer().getOnlinePlayers()) {
                    if (StringUtil.startsWithIgnoreCase(player.getName(), args[0])) {
                        completions.add(player.getName());
                    }
                }
                break;
            case 2:
                List<String> subcommands = Arrays.asList("extended", "only");
                StringUtil.copyPartialMatches(args[1], subcommands, completions);
                break;
            case 3:
                // Only send statistic name suggestions in /stats <player> only
                if (!args[1].equalsIgnoreCase("only")) {
                    break;
                }

                for (Statistic statistic : Statistic.values()) {
                    if (StringUtil.startsWithIgnoreCase(statistic.name(), args[2])) {
                        completions.add(statistic.name().toLowerCase());
                    }
                }
        }

        Collections.sort(completions);
        return completions;
    }

    private void sendStatistics(CommandSender sender, Player player, Boolean extended) {
        sender.sendMessage("- Deaths: " + player.getStatistic(Statistic.DEATHS));
        sender.sendMessage("- Mob kills: " + player.getStatistic(Statistic.MOB_KILLS));
        sender.sendMessage("- Player kills: " + player.getStatistic(Statistic.PLAYER_KILLS));
        sender.sendMessage("- Time played: "
                + TimeUtil.toPrettyTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE)));
        sender.sendMessage("- Time since last death: "
                + TimeUtil.toPrettyTime(player.getStatistic(Statistic.TIME_SINCE_DEATH)));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sender.sendMessage(
                "- Registered since: " + dateFormat.format(new Date(player.getFirstPlayed())));

        if (extended) {
            sender.sendMessage("- Distance by elytra: "
                    + toSIPrefix(player.getStatistic(Statistic.AVIATE_ONE_CM)) + "m");
            sender.sendMessage("- Distance by minecart: "
                    + toSIPrefix(player.getStatistic(Statistic.MINECART_ONE_CM)) + "m");
            sender.sendMessage("- Distance by horse: "
                    + toSIPrefix(player.getStatistic(Statistic.HORSE_ONE_CM)) + "m");
            sender.sendMessage("- Distance walked: "
                    + toSIPrefix(player.getStatistic(Statistic.WALK_ONE_CM)) + "m");
            sender.sendMessage("- Damage taken: " + player.getStatistic(Statistic.DAMAGE_TAKEN));
            sender.sendMessage("- Damage dealt: " + player.getStatistic(Statistic.DAMAGE_DEALT));
            sender.sendMessage("- Times jumped: " + player.getStatistic(Statistic.JUMP));
            sender.sendMessage("- Raids won: " + player.getStatistic(Statistic.RAID_WIN));
            sender.sendMessage("- Diamonds picked up: "
                    + player.getStatistic(Statistic.PICKUP, Material.DIAMOND));
        }
    }

    // converts numbers to their SI prefix laden counterparts
    private static String toSIPrefix(double number) {
        if (number < 100) {
            return number + " c";
        } else if (number < 100000) {
            number = Math.round(number / 100);
            return String.valueOf(number);
        } else if (number >= 100000) {
            // Divides by 1000 to allow for two significant digits
            number = Math.round(number / 1000);
            // Divides by 100 to finally get to km
            number /= 100;
            return String.format("%.2f k", number);
        }
        return null;
    }
}
