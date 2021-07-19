package com.hackclub.hccore.utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TutorialUtil {
    public static final String greeting = ChatColor.RED + "Greetings! Welcome to the Hack Club vanilla Minecraft server!";

    public static final String[] tutorial = {"Use /nick to set your nick name and /color to set your chat and name colors", ChatColor.RED + "Rules:", "Be nice.",  "No griefing or stealing", "No mods that give an unfair advantage", "Follow Hack Club CoC", "If you want to contribute to plugin development head on over to the GitHub: " + ChatColor.RED + "https://github.com/hackclub/HCCore/. ", "Type /tutorial to see this at any time and /discord to join the Discord!" + ChatColor.BLUE + ChatColor.BOLD + "Also check out the modded server - IP: modded-mc.hackclub.com"};

    public static void send(Player player, boolean withIntro) {
        if (withIntro) {
            player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + greeting);
        }
        for (String message : tutorial) {
            player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + message);
        }
    }
}
