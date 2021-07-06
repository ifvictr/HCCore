package com.hackclub.hccore.commands;

import com.hackclub.hccore.HCCorePlugin;
import com.hackclub.hccore.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand implements CommandExecutor {
    private final HCCorePlugin plugin;

    public NickCommand(HCCorePlugin plugin) {
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

        Player player = (Player) sender;
        if (args.length == 0) {
            this.plugin.getDataManager().getData(player).setNickname(null);
            sender.sendMessage(
                ChatColor.GREEN + "Your nickname has been reset!"
            );
            return true;
        }

        String newNickname = String.join(" ", args);
        if (newNickname.equalsIgnoreCase("Saharsh")) {
            this.plugin.getDataManager()
                .getData(player)
                .setNickname("Saharchery");
            player.kickPlayer("Kicked for being Saharsh.");
            return true;
        }

        if (newNickname.length() > PlayerData.MAX_NICKNAME_LENGTH) {
            sender.sendMessage(
                ChatColor.RED +
                "Your nickname can’t be longer than " +
                PlayerData.MAX_NICKNAME_LENGTH +
                " characters"
            );
            return true;
        }

        this.plugin.getDataManager().getData(player).setNickname(newNickname);
        sender.sendMessage(
            ChatColor.GREEN +
            "Your nickname was set to " +
            this.plugin.getDataManager().getData(player).getNameColor() +
            newNickname
        );

        return true;
    }
}
