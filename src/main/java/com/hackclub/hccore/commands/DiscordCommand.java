package com.hackclub.hccore.commands;

import com.hackclub.hccore.HCCorePlugin;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DiscordCommand implements CommandExecutor {
    private HCCorePlugin plugin;

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
