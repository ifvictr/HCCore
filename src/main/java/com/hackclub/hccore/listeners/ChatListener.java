package com.hackclub.hccore.listeners;

import java.util.logging.Logger;
import java.util.logging.Level;
import com.hackclub.hccore.HCCorePlugin;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.hackclub.hccore.GrpcClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.github.cdimascio.dotenv.Dotenv;

public class ChatListener implements Listener {
    private final HCCorePlugin plugin;
    private final GrpcClient grpcClient;

    public ChatListener(HCCorePlugin plugin, Dotenv dotenv) {
        this.plugin = plugin;

        String target = dotenv.get("GRPC_SERVER");
        if (target == null || target.isEmpty()) {
            this.plugin.getLogger().log(Level.WARNING, "GRPC_SERVER environment variable null or empty");
        }

        // disable TLS to avoid needing certificates.
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
            .usePlaintext()
            .build();

        this.grpcClient = new GrpcClient(plugin, channel);
    }

    @EventHandler
    public void onChatChange(final AsyncPlayerChatEvent event) {
        // TODO: 'player' includes tokens for changing color, etc.
        String player = event.getPlayer().getDisplayName();
        String text = event.getMessage();

        this.grpcClient.sendToSlackWrapper(player, text);
    }
}
