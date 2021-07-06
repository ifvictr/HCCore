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
import com.hackclub.hccore.listeners.NameChangeListener;
import com.hackclub.hccore.listeners.PlayerListener;
import com.hackclub.hccore.listeners.SleepListener;
import com.hackclub.hccore.tasks.AutoAFKTask;
import com.hackclub.hccore.tasks.CheckAdAstraTask;
import com.hackclub.hccore.utils.TimeUtil;
import hu.trigary.advancementcreator.Advancement;
import hu.trigary.advancementcreator.AdvancementFactory;
import hu.trigary.advancementcreator.shared.ItemObject;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

public class HCCorePlugin extends JavaPlugin {
    private DataManager dataManager;
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        // Disable default advancement announcements
        for (World world : this.getServer().getWorlds()) {
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        }

        // Create config
        this.saveDefaultConfig();

        // Load managers
        this.dataManager = new DataManager(this);
        this.protocolManager = ProtocolLibrary.getProtocolManager();

        // Register commands
        this.getCommand("afk").setExecutor(new AFKCommand(this));
        this.getCommand("color").setExecutor(new ColorCommand(this));
        this.getCommand("downvote").setExecutor(new DownvoteCommand(this));
        this.getCommand("loc").setExecutor(new LocCommand(this));
        this.getCommand("nick").setExecutor(new NickCommand(this));
        this.getCommand("ping").setExecutor(new PingCommand(this));
        this.getCommand("shrug").setExecutor(new ShrugCommand(this));
        this.getCommand("spawn").setExecutor(new SpawnCommand(this));
        this.getCommand("stats").setExecutor(new StatsCommand(this));
        this.getCommand("tableflip").setExecutor(new TableflipCommand(this));
        this.getCommand("upvote").setExecutor(new UpvoteCommand(this));

        // Register event listeners
        this.getServer()
            .getPluginManager()
            .registerEvents(new AdvancementListener(this), this);
        this.getServer()
            .getPluginManager()
            .registerEvents(new AFKListener(this), this);
        this.getServer()
            .getPluginManager()
            .registerEvents(new BeehiveInteractionListener(this), this);
        this.getServer()
            .getPluginManager()
            .registerEvents(new PlayerListener(this), this);
        this.getServer()
            .getPluginManager()
            .registerEvents(new SleepListener(this), this);

        // Register packet listeners
        this.getProtocolManager()
            .addPacketListener(
                new NameChangeListener(
                    this,
                    ListenerPriority.NORMAL,
                    PacketType.Play.Server.PLAYER_INFO
                )
            );

        // Register tasks
        new AutoAFKTask(this)
        .runTaskTimer(
                this,
                this.getConfig().getInt("settings.auto-afk-time") *
                TimeUtil.TICKS_PER_SECOND,
                30 * TimeUtil.TICKS_PER_SECOND
            );
        new CheckAdAstraTask(this)
        .runTaskTimer(this, 0, 10 * TimeUtil.TICKS_PER_SECOND);

        // Register advancements
        this.registerAdvancements();

        // Register all the players that were online before this plugin was enabled
        // (example
        // scenario: plugin reload) to prevent null pointer errors.
        this.getDataManager().registerAll();
    }

    @Override
    public void onDisable() {
        this.getDataManager().unregisterAll();
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public ProtocolManager getProtocolManager() {
        return this.protocolManager;
    }

    private void registerAdvancements() {
        AdvancementFactory factory = new AdvancementFactory(this, false, false);

        // Create root advancement
        ItemObject hackClubBanner = new ItemObject()
            .setItem(Material.RED_BANNER)
            .setNbt(
                "{BlockEntityTag:{Patterns:[{Color:0,Pattern:\"rs\"},{Color:14,Pattern:\"hh\"},{Color:0,Pattern:\"ls\"},{Color:0,Pattern:\"ms\"},{Color:14,Pattern:\"bo\"}]}}"
            );
        Advancement root = new Advancement(
            new NamespacedKey(this, "root"),
            hackClubBanner,
            new TextComponent("Hack Club"),
            new TextComponent("Beep boop beep beep boop")
        )
            .makeRoot("block/coal_block", true)
            .setFrame(Advancement.Frame.TASK);

        // Create regular advancements
        Advancement allMusicDiscs = factory
            .getAllItems(
                "all_music_discs",
                root,
                "Musicophile",
                "Collect every single music disc",
                Material.JUKEBOX,
                Material.MUSIC_DISC_11,
                Material.MUSIC_DISC_13,
                Material.MUSIC_DISC_BLOCKS,
                Material.MUSIC_DISC_CAT,
                Material.MUSIC_DISC_CHIRP,
                Material.MUSIC_DISC_FAR,
                Material.MUSIC_DISC_MALL,
                Material.MUSIC_DISC_MELLOHI,
                Material.MUSIC_DISC_PIGSTEP,
                Material.MUSIC_DISC_STAL,
                Material.MUSIC_DISC_STRAD,
                Material.MUSIC_DISC_WAIT,
                Material.MUSIC_DISC_WARD
            )
            .setFrame(Advancement.Frame.CHALLENGE);

        Advancement findBug = factory.getImpossible(
            "find_bug",
            root,
            "Bug Squasher",
            "Find and report a bug",
            Material.IRON_BOOTS
        );
        Advancement contribute = factory.getImpossible(
            "contribute",
            findBug,
            "pairsOfHands++",
            "Contribute to the server’s codebase on GitHub",
            Material.COMMAND_BLOCK
        );

        Advancement mineDiamondOre = factory.getImpossible(
            "mine_diamond_ore",
            root,
            "Look Ma, Diamonds!",
            "Find your first diamond while mining",
            Material.DIAMOND_ORE
        );
        Advancement connectToNetherHub = factory
            .getImpossible(
                "connect_to_nether_hub",
                mineDiamondOre,
                "Linked Up",
                "Connect your base to the Nether hub",
                Material.POWERED_RAIL
            )
            .setFrame(Advancement.Frame.GOAL);
        Advancement killDragonInsane = factory
            .getCountedImpossible(
                "kill_dragon_insane",
                connectToNetherHub,
                "Dragon Master",
                "Kill the Ender Dragon 10 times",
                Material.DRAGON_HEAD,
                10
            )
            .setFrame(Advancement.Frame.CHALLENGE)
            .setHidden(false);
        Advancement killWitherInsane = factory
            .getCountedImpossible(
                "kill_wither_insane",
                killDragonInsane,
                "Are You Insane?!",
                "Kill the Wither 20 times",
                Material.WITHER_SKELETON_SKULL,
                20
            )
            .setFrame(Advancement.Frame.CHALLENGE);
        Advancement killElderGuardian = factory
            .getKill(
                "kill_elder_guardian",
                mineDiamondOre,
                "The Deep End",
                "Defeat an Elder Guardian",
                Material.PRISMARINE_SHARD,
                EntityType.ELDER_GUARDIAN
            )
            .setFrame(Advancement.Frame.GOAL);
        Advancement killWolf = factory
            .getKill(
                "kill_wolf",
                mineDiamondOre,
                "You Monster!",
                "Slaughter a doggo",
                Material.BONE,
                EntityType.WOLF
            )
            .setFrame(Advancement.Frame.TASK);
        Advancement killIronGolem = factory
            .getKill(
                "kill_iron_golem",
                killWolf,
                "Well That’s IRONic…",
                "Kill an Iron Golem",
                Material.IRON_INGOT,
                EntityType.IRON_GOLEM
            )
            .setFrame(Advancement.Frame.TASK);
        Advancement millionMiler = factory
            .getImpossible(
                "million_miler",
                mineDiamondOre,
                "Million Miler",
                "Fly one million miles (1,609,344 km) with an elytra",
                Material.ELYTRA
            )
            .setFrame(Advancement.Frame.CHALLENGE);
        Advancement adAstra = factory
            .getImpossible(
                "ad_astra",
                millionMiler,
                "Ad Astra",
                "Reach outer space and touch the stars",
                Material.FIREWORK_ROCKET
            )
            .setFrame(Advancement.Frame.CHALLENGE);

        // Activate all the advancements
        List<Advancement> advancements = new ArrayList<Advancement>() {
            private static final long serialVersionUID = 0L;

            {
                add(root);
                add(allMusicDiscs);
                add(findBug);
                add(contribute);
                add(mineDiamondOre);
                add(connectToNetherHub);
                add(killDragonInsane);
                add(killWitherInsane);
                add(killElderGuardian);
                add(killWolf);
                add(killIronGolem);
                add(millionMiler);
                add(adAstra);
            }
        };
        for (Advancement advancement : advancements) {
            if (this.getServer().getAdvancement(advancement.getId()) == null) {
                advancement.activate(false);
            }
        }

        // Reload the data cache after all advancements have been added
        this.getServer().reloadData();
    }
}
