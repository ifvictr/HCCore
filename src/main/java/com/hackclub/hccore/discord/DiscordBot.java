 package com.hackclub.hccore.discord;

import com.hackclub.hccore.DataManager;
import com.hackclub.hccore.HCCorePlugin;
import com.hackclub.hccore.PlayerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class DiscordBot extends ListenerAdapter implements Listener {
    private JDA jda;
    private final HCCorePlugin plugin;
    private TextChannel channel;
    private final String prefix;

    public DiscordBot(HCCorePlugin plugin) {
        this.plugin = plugin;

        JDABuilder builder = JDABuilder.createDefault(this.plugin.getConfig().getString("settings.discord-link.bot-token"));
        builder.setActivity(Activity.playing("mc.hackclub.com"));
        try {
            this.jda = builder.build();
        } catch (LoginException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Error logging into Discord!");
            e.printStackTrace();
        }

        jda.addEventListener(this);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.prefix = plugin.getConfig().getString("settings.discord-link.bot-prefix");
    }

    @Override
    public void onReady(ReadyEvent e) {
        this.channel = jda.getTextChannelById(this.plugin.getConfig().getLong("settings.discord-link.channel-id"));
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setAuthor("Server Started!");
        this.channel.sendMessage(embed.build()).queue();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMember() == null || event.getAuthor().equals(jda.getSelfUser())) return;

        if (event.getChannel() == this.channel) {
            // list command
            if (event.getMessage().getContentRaw().equals(prefix + "list")) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(Bukkit.getOnlinePlayers().size() + " / " + Bukkit.getMaxPlayers() + " Players Online");
                StringBuilder listBuilder = new StringBuilder();
                Bukkit.getOnlinePlayers().forEach(player -> {
                    PlayerData data = plugin.getDataManager().getData(player);
                    listBuilder.append("\n • " + ChatColor.stripColor(data.getDisplayedName()));
                });
                embed.setDescription(listBuilder.toString());
                embed.setColor(Color.CYAN);
                this.channel.sendMessage(embed.build()).queue();
                return;
            }

            TextComponent prefixComponent = new TextComponent("[Discord] ");
            prefixComponent.setColor(ChatColor.BLUE.asBungee());

            TextComponent nameComponent = new TextComponent(event.getMember().getEffectiveName());
            nameComponent.setColor(event.getMember().getColor() != null ? net.md_5.bungee.api.ChatColor.of(event.getMember().getColor()) : ChatColor.WHITE.asBungee());
            nameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(event.getAuthor().getAsTag())));

            TextComponent arrowComponent = new TextComponent(" » ");
            arrowComponent.setColor(ChatColor.GOLD.asBungee());

            TextComponent playerChatComponent =
                    new TextComponent(ChatColor.translateAlternateColorCodes('&', event.getMessage().getContentDisplay()));
            playerChatComponent.setColor(ChatColor.WHITE.asBungee());
            plugin.getServer().spigot().broadcast(prefixComponent, nameComponent, arrowComponent, playerChatComponent);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        PlayerData player = plugin.getDataManager().getData(e.getPlayer());
        channel.sendMessage(ChatColor.stripColor(player.getDisplayedName()) + " » " + ChatColor.stripColor(e.getMessage())).queue();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setAuthor(ChatColor.stripColor(player.getDisplayName()) + " joined the game!", null, getAvatarUrl(player));
        channel.sendMessage(embed.build()).queue();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setAuthor(ChatColor.stripColor(player.getDisplayName()) + " left the game!", null, getAvatarUrl(player));
        channel.sendMessage(embed.build()).queue();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent e) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.BLACK);
        embed.setAuthor(e.getDeathMessage(), null, getAvatarUrl(e.getEntity()));
        channel.sendMessage(embed.build()).queue();
    }

    /*@EventHandler(priority = EventPriority.MONITOR)
    public void onAchievement(PlayerAdvancementDoneEvent e) {
        if(e.getAdvancement() == null || e.getAdvancement().getKey().getKey().contains("recipe/") || e.getPlayer() == null) return;

        CraftAdvancement craftAdvancement = (CraftAdvancement) e.getAdvancement();
        String title = craftAdvancement.getHandle().c().a().getText();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.YELLOW);
        embed.setAuthor(ChatColor.stripColor(e.getPlayer().getDisplayName()) + " has made the advancement " + title, null, getAvatarUrl(e.getPlayer()));
        channel.sendMessage(embed.build()).queue();
    }*/

    private String getAvatarUrl(Player player) {
        return "https://cravatar.eu/helmavatar/" + player.getUniqueId() + ".png";
    }

    public void close() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setAuthor("Server Stopped!");
        this.channel.sendMessage(embed.build()).queue();
        this.jda.shutdown();
    }
}
