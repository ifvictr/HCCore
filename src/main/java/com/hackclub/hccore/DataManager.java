package com.hackclub.hccore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class DataManager {
    private final HCCorePlugin plugin;
    private final String dataFolder;
    private final Map<UUID, PlayerData> players = new HashMap<>();

    public DataManager(HCCorePlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder() + File.separator + "players";

        File folder = new File(this.dataFolder);
        folder.mkdirs();
    }

    public String getDataFolder() {
        return this.dataFolder;
    }

    public PlayerData getData(Player player) {
        return this.players.get(player.getUniqueId());
    }

    public void registerPlayer(Player player) {
        this.players.put(
                player.getUniqueId(),
                new PlayerData(this.plugin, player)
            );

        // Register player's team
        Scoreboard mainScoreboard = player
            .getServer()
            .getScoreboardManager()
            .getMainScoreboard();
        player.setScoreboard(mainScoreboard);
        // Unregister existing teams in the player's name
        Team playerTeam = mainScoreboard.getTeam(player.getName());
        if (playerTeam != null) {
            playerTeam.unregister();
        }
        playerTeam = mainScoreboard.registerNewTeam(player.getName());
        playerTeam.addEntry(player.getName());

        // Load in player data for use
        this.getData(player).load();
    }

    public void registerAll() {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            this.registerPlayer(player);
        }
    }

    public void unregisterPlayer(Player player) {
        this.getData(player).save();

        this.getData(player).getTeam().unregister();

        this.players.remove(player.getUniqueId());
    }

    public void unregisterAll() {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            this.unregisterPlayer(player);
        }
    }
}
