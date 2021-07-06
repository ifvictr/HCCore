package com.hackclub.hccore;

import com.google.gson.annotations.Expose;
import com.hackclub.hccore.events.player.PlayerAFKStatusChangeEvent;
import com.hackclub.hccore.utils.gson.GsonUtil;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Team;

public class PlayerData {
    public static final int MAX_NICKNAME_LENGTH = 16;

    private final HCCorePlugin plugin;
    private final Player player;
    private final File dataFile;

    // Session data (will be cleared when player quits)
    private boolean isAfk = false;
    private long lastDamagedAt = 0;
    private long lastActiveAt = 0;

    // Persistent data
    @Expose
    private String nickname = null;

    @Expose
    private String slackId = null;

    @Expose
    private ChatColor nameColor = ChatColor.WHITE;

    @Expose
    private ChatColor messageColor = ChatColor.GRAY;

    @Expose
    private Map<String, Location> savedLocations = new LinkedHashMap<>();

    public PlayerData(HCCorePlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.dataFile =
            new File(
                plugin.getDataManager().getDataFolder(),
                player.getUniqueId() + ".json"
            );
    }

    public boolean isAfk() {
        return this.isAfk;
    }

    public void setAfk(boolean afk) {
        this.isAfk = afk;
        this.updateDisplayedName();

        ChatColor newColor = afk ? ChatColor.GRAY : this.getNameColor();
        String newSuffix = afk ? " (AFK)" : "";
        this.getTeam().setColor(newColor);
        this.getTeam().setSuffix(newSuffix);

        Event event = new PlayerAFKStatusChangeEvent(this.player, afk);
        this.player.getServer().getPluginManager().callEvent(event);
    }

    public long getLastDamagedAt() {
        return this.lastDamagedAt;
    }

    public void setLastDamagedAt(long damagedAt) {
        this.lastDamagedAt = damagedAt;
    }

    public long getLastActiveAt() {
        return this.lastActiveAt;
    }

    public void setLastActiveAt(long activeAt) {
        this.lastActiveAt = activeAt;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        // Validate length if a nickname was specified
        if (
            nickname != null &&
            nickname.length() > PlayerData.MAX_NICKNAME_LENGTH
        ) {
            return;
        }

        String oldName = this.getUsableName();
        this.nickname = nickname;
        this.updateDisplayedName();

        // Team#addEntry takes in a string, which in our case, will be a player name. We
        // have to
        // remove the old name and add the new one so the game has a reference to the
        // player.
        this.getTeam().removeEntry(oldName);
        this.getTeam().addEntry(this.getUsableName());
    }

    public String getSlackId() {
        return this.slackId;
    }

    public void setSlackId(String id) {
        this.slackId = id;
    }

    public ChatColor getNameColor() {
        return this.nameColor;
    }

    public void setNameColor(ChatColor color) {
        this.nameColor =
            (color != null && color.isColor()) ? color : ChatColor.WHITE;
        this.updateDisplayedName();

        this.getTeam().setColor(this.getNameColor());
    }

    public ChatColor getMessageColor() {
        return this.messageColor;
    }

    public void setMessageColor(ChatColor color) {
        this.messageColor =
            (color != null && color.isColor()) ? color : ChatColor.GRAY;
    }

    public Map<String, Location> getSavedLocations() {
        return this.savedLocations;
    }

    public Team getTeam() {
        return this.player.getServer()
            .getScoreboardManager()
            .getMainScoreboard()
            .getTeam(this.player.getName());
    }

    public void load() {
        try {
            this.dataFile.getParentFile().mkdirs();

            if (!this.dataFile.exists()) {
                this.plugin.getLogger()
                    .log(
                        Level.INFO,
                        "No data file found for " +
                        this.player.getName() +
                        ", creating…"
                    );
                this.dataFile.createNewFile();
                this.save();
            }

            // Populate this instance
            PlayerData data = GsonUtil
                .getInstance()
                .fromJson(new FileReader(this.dataFile), PlayerData.class);
            this.setNickname(data.nickname);
            this.setSlackId(data.slackId);
            this.setNameColor(data.nameColor);
            this.setMessageColor(data.messageColor);
            this.savedLocations = data.savedLocations;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            this.dataFile.getParentFile().mkdirs();

            FileWriter writer = new FileWriter(this.dataFile);
            GsonUtil.getInstance().toJson(this, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsableName() {
        return this.getNickname() != null
            ? this.getNickname()
            : this.player.getName();
    }

    public String getDisplayedName() {
        String format = "%s";
        if (this.isAfk()) {
            format = ChatColor.GRAY + format + " (AFK)";
        } else {
            format = this.getNameColor() + format;
        }

        return String.format(format, this.getUsableName());
    }

    private void updateDisplayedName() {
        String builtDisplayName = this.getDisplayedName();
        this.player.setDisplayName(builtDisplayName);
        this.player.setPlayerListName(builtDisplayName);
        this.refreshNameTag();
    }

    private void refreshNameTag() {
        for (Player onlinePlayer : this.player.getServer().getOnlinePlayers()) {
            if (
                onlinePlayer.equals(this.player) ||
                !onlinePlayer.canSee(this.player)
            ) {
                continue;
            }
            // Showing the player again after they've been hidden causes a Player Info
            // packet to be
            // sent, which is then intercepted by NameChangeListener. The actual
            // modification of the
            // name tag takes place there.
            onlinePlayer.hidePlayer(this.plugin, this.player);
            onlinePlayer.showPlayer(this.plugin, this.player);
        }
    }
}
