package com.hackclub.hccore.listeners;

import com.hackclub.hccore.HCCorePlugin;
import de.tr7zw.changeme.nbtapi.NBTTileEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BeehiveInteractionListener implements Listener {
    private final HCCorePlugin plugin;

    public BeehiveInteractionListener(HCCorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        // Check if this was a right hand interaction with a block while not holding
        // anything
        if (
            !(
                event.getHand() == EquipmentSlot.HAND &&
                event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                event
                    .getPlayer()
                    .getInventory()
                    .getItemInMainHand()
                    .getType() ==
                Material.AIR
            )
        ) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        // Check if it's a bee nest or beehive
        if (
            !(
                clickedBlock.getType() == Material.BEE_NEST ||
                clickedBlock.getType() == Material.BEEHIVE
            )
        ) {
            return;
        }

        NBTTileEntity tile = new NBTTileEntity(clickedBlock.getState());
        int beeCount = tile.getCompoundList("Bees").size();
        int honeyLevel =
            ((Beehive) clickedBlock.getBlockData()).getHoneyLevel();
        event
            .getPlayer()
            .sendMessage(
                "There are " +
                beeCount +
                " bees housed and the honey level is " +
                honeyLevel
            );
    }
}
