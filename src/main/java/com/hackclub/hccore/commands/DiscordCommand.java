package com.hackclub.hccore.commands;

import com.hackclub.hccore.HCCorePlugin;

import com.comphenix.protocol.PacketType;
import com.hackclub.hccore.HCCorePlugin;
import com.hackclub.hccore.discord.DiscordBot;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;


public class DiscordCommand implements CommandExecutor {
    private final HCCorePlugin plugin;

    public DiscordCommand(HCCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String inviteLink = plugin.getConfig().getString("discord-invite-url");

        TextComponent component = new TextComponent("Join our Discord Server: ");
        component.setColor(ChatColor.GREEN.asBungee());

        TextComponent inviteComponent = new TextComponent(inviteLink);
        inviteComponent.setColor(ChatColor.BLUE.asBungee());
        inviteComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, inviteLink));
        inviteComponent.setUnderlined(true);
        sender.spigot().sendMessage(component, inviteComponent);

        return true;
    }
}
