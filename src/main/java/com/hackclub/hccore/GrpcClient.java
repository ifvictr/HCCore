package com.hackclub.hccore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.hackclub.hccore.commands.AFKCommand;
import com.hackclub.hccore.commands.ColorCommand;
import com.hackclub.hccore.commands.DownvoteCommand;
import com.hackclub.hccore.commands.LocCommand;
import com.hackclub.hccore.commands.NickCommand;
import com.hackclub.hccore.commands.PingCommand;
import com.hackclub.hccore.commands.ShrugCommand;
import com.hackclub.hccore.commands.SpawnCommand;
import com.hackclub.hccore.commands.StatsCommand;
import com.hackclub.hccore.commands.TableflipCommand;
import com.hackclub.hccore.commands.UpvoteCommand;
import com.hackclub.hccore.listeners.AFKListener;
import com.hackclub.hccore.listeners.AdvancementListener;
import com.hackclub.hccore.listeners.BeehiveInteractionListener;
import com.hackclub.hccore.listeners.ChatListener;
import com.hackclub.hccore.listeners.NameChangeListener;
import com.hackclub.hccore.listeners.PlayerListener;
import com.hackclub.hccore.listeners.SleepListener;
import com.hackclub.hccore.tasks.AutoAFKTask;
import com.hackclub.hccore.tasks.CheckAdAstraTask;
import com.hackclub.hccore.utils.TimeUtil;
import hu.trigary.advancementcreator.Advancement;
import hu.trigary.advancementcreator.AdvancementFactory;
import hu.trigary.advancementcreator.shared.ItemObject;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

public class GrpcClient {
  private final HCCorePlugin plugin;
  private final MessengerGrpc.MessengerBlockingStub blockingStub;

  public GrpcClient(HCCorePlugin plugin, Channel channel) {
    this.plugin = plugin;
    
    // 'channel' here is a Channel, not a ManagedChannel
    // so it is not this code's responsibility to shut it down.
    blockingStub = MessengerGrpc.newBlockingStub(channel);
  }

  public void sendToSlackWrapper(String username, String text) {
     SlackMessageRequest request = SlackMessageRequest
      .newBuilder()
      .setPlayer(username)
      .setText(text)
      .build();

      SlackMessageReply response;

      try {
        response = blockingStub.sendSlackMessage(request);

        // TODO: check to see if response contains success
      } catch (StatusRuntimeException e) {
         this.plugin.getLogger().log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      }
  }
}
