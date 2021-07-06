package com.hackclub.hccore.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.hackclub.hccore.HCCorePlugin;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

// Modified from https://gist.github.com/Techcable/d574a6e78ada3b8511bd
public class NameChangeListener extends PacketAdapter {
    private final HCCorePlugin plugin;

    public NameChangeListener(
        HCCorePlugin plugin,
        ListenerPriority listenerPriority,
        PacketType... types
    ) {
        super(params(plugin, types).listenerPriority(listenerPriority));
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(final PacketEvent event) {
        // Only intercept packets that add players to the list
        if (
            event.getPacket().getPlayerInfoAction().read(0) !=
            EnumWrappers.PlayerInfoAction.ADD_PLAYER
        ) {
            return;
        }

        List<PlayerInfoData> playerInfoDataList = event
            .getPacket()
            .getPlayerInfoDataLists()
            .read(0);
        List<PlayerInfoData> newPlayerInfoDataList = new ArrayList<>();
        for (PlayerInfoData playerInfoData : playerInfoDataList) {
            Player player = event
                .getPlayer()
                .getServer()
                .getPlayer(playerInfoData.getProfile().getUUID());
            // If any of this doesn't exist but it's in the list, just add it to the new one
            // and forget about it.
            if (
                playerInfoData == null ||
                playerInfoData.getProfile() == null ||
                player == null
            ) {
                newPlayerInfoDataList.add(playerInfoData);
                continue;
            }

            // Create a profile with a custom name from current one
            String newName =
                this.plugin.getDataManager().getData(player).getUsableName();
            WrappedGameProfile newProfile = playerInfoData
                .getProfile()
                .withName(newName);
            // Copy properties (currently just skin texture) to new profile
            newProfile
                .getProperties()
                .putAll(playerInfoData.getProfile().getProperties());

            // Put all of the info from the old profile into the new one
            PlayerInfoData newPlayerInfoData = new PlayerInfoData(
                newProfile,
                playerInfoData.getLatency(),
                playerInfoData.getGameMode(),
                playerInfoData.getDisplayName()
            );

            newPlayerInfoDataList.add(newPlayerInfoData);
        }

        // Send the modified list
        event
            .getPacket()
            .getPlayerInfoDataLists()
            .write(0, newPlayerInfoDataList);
    }
}
