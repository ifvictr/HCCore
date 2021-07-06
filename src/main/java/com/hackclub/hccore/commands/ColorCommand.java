package com.hackclub.hccore.commands;

import com.hackclub.hccore.HCCorePlugin;
import com.hackclub.hccore.PlayerData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class ColorCommand implements TabExecutor {
    private static final List<String> COLOR_NAMES = Arrays
        .asList(ChatColor.values())
        .stream()
        .filter(value -> value.isColor())
        .map(color -> color.name().toLowerCase())
        .collect(Collectors.toList());

    private final HCCorePlugin plugin;

    public ColorCommand(HCCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
        CommandSender sender,
        Command cmd,
        String alias,
        String[] args
    ) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                ChatColor.RED + "You must be a player to use this"
            );
            return true;
        }

        if (args.length == 0) {
            return false;
        }

        Player player = (Player) sender;
        PlayerData data = this.plugin.getDataManager().getData(player);

        // Validate selected color
        ChatColor newColor = null;
        if (args.length > 1) {
            // Not in ChatColor at all
            if (!COLOR_NAMES.contains(args[1].toLowerCase())) {
                sender.sendMessage(ChatColor.RED + "Invalid color specified");
                return true;
            }

            // Is in ChatColor, but not a color
            newColor = ChatColor.valueOf(args[1].toUpperCase());
            if (!newColor.isColor()) {
                sender.sendMessage(ChatColor.RED + "Invalid color specified");
                return true;
            }
        }

        switch (args[0].toLowerCase()) {
            // /color chat [color]
            case "chat":
                if (args.length == 1) {
                    data.setMessageColor(null);
                    sender.sendMessage("Your chat color has been reset");
                    break;
                }
                data.setMessageColor(newColor);
                sender.sendMessage(
                    "Your chat color has been set to " + newColor + "this color"
                );
                break;
            // /color name [color]
            case "name":
                if (args.length == 1) {
                    data.setNameColor(null);
                    sender.sendMessage("Your name color has been reset");
                    break;
                }
                data.setNameColor(newColor);
                sender.sendMessage(
                    "Your name color has been set to " + newColor + "this color"
                );
                break;
            default:
                return false;
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
        if (!(sender instanceof Player)) {
            return null;
        }

        List<String> completions = new ArrayList<String>();
        switch (args.length) {
            // Complete subcommand
            case 1:
                List<String> subcommands = Arrays.asList("chat", "name");
                StringUtil.copyPartialMatches(
                    args[0],
                    subcommands,
                    completions
                );
                break;
            // Complete color name for /color chat and /color name
            case 2:
                if (
                    !(
                        args[0].equalsIgnoreCase("chat") ||
                        args[0].equalsIgnoreCase("name")
                    )
                ) {
                    break;
                }

                StringUtil.copyPartialMatches(
                    args[1],
                    COLOR_NAMES,
                    completions
                );
                break;
        }

        Collections.sort(completions);
        return completions;
    }
}
