
// Author: Dashie
// Version: 1.0

// partly coded while high as feck.  Config coming soon, not now though, this will be a bigger plugin in the future!
// Actually, the ideology applied to the code in this file will be used for further extension in the future. #highasakite


package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MegaTNT extends JavaPlugin
{
    protected final JavaPlugin plugin = this;

    protected final class Events implements Listener
    {
        protected final List<Location> locations = new ArrayList<>();

        @EventHandler protected final void onTNTIgnite(final PlayerInteractEvent e)
        {
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            {
                final Player p = e.getPlayer();

                if (e.getItem() != null)
                {
                    if (e.getItem().getType().equals(Material.FLINT_AND_STEEL))
                    {
                        final Block block = e.getClickedBlock();

                        if (block != null)
                        {
                            if (block.getType().equals(Material.TNT))
                            {
                                final Location location = block.getLocation();

                                if (locations.contains(location))
                                {
                                    locations.remove(location);

                                    final World world = location.getWorld();

                                    getServer().getScheduler().runTaskLater
                                    (
                                        plugin,

                                        () -> world.createExplosion(location, 40, true, true),

                                        80
                                    );
                                };
                            };
                        };
                    };
                };
            };
        };

        @EventHandler protected final void onBlockPlace(final BlockPlaceEvent e)
        {
            final Player p = e.getPlayer();

            if (p.getInventory().getItemInMainHand().isSimilar(TNT.MegaTNT))
            {
                final Location location = e.getBlock().getLocation();

                getServer().getScheduler().runTaskLater
                (
                    plugin,

                    () ->
                    {
                        if (location.getWorld().getBlockAt(location).equals(e.getBlock()))
                        {
                            locations.add(location);
                        };
                    },

                    2
                );
            };
        };

        @EventHandler protected final void onBlockBreak(final BlockBreakEvent e)
        {
            final Block block = e.getBlock();

            if (locations.contains(block.getLocation()))
            {
                if (block.getType().equals(Material.TNT))
                {
                    locations.remove(block.getLocation());
                };
            };
        };
    };

    @Override public final void onEnable()
    {
        print("Loading ....");

        getServer().getPluginManager().registerEvents(new Events(), this);
        getCommand("megatnt").setExecutor(new Commands());

        print("Plugin: MegaTNT");
        print("Author: Dashie");
        print("Version: 1.0");
        print("Github: https://github.com/KvinneKraft");
        print("Email: KvinneKraft@protonmail.com");

        print("Done!");
    };

    public class TNT
    {
        public final ItemStack MegaTNT = new ItemStack(Material.TNT, 1);

        public TNT()
        {
            final ItemMeta MegaMeta = MegaTNT.getItemMeta();

            MegaMeta.setDisplayName(color("&cExplosive T.N.T."));
            MegaMeta.setLore(Arrays.asList(color("&aPlace me down and light me.")));
            MegaMeta.setCustomModelData(2020);

            MegaTNT.setItemMeta(MegaMeta);
        };
    };

    protected final TNT TNT = new TNT();

    protected final class Commands implements CommandExecutor
    {
        @Override public final boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("Only players may use this!");
                return false;
            };

            final Player p = (Player) s;

            if (p.isOp())
            {
                if (as.length > 0)
                {
                    if (as[0].equalsIgnoreCase("get"))
                    {
                        int amount = 1;

                        if (as.length > 1)
                        {
                            try
                            {
                                amount = Integer.parseInt(as[1]);
                            }

                            catch (final Exception e)
                            {
                                p.sendMessage(color("&cThe amount value must be integral!"));
                                return false;
                            };
                        };

                        TNT.MegaTNT.setAmount(amount);

                        p.getInventory().addItem(TNT.MegaTNT);
                        p.sendMessage(color("&aYou have given yourself some TNT!"));

                        return true;
                    };
                };

                p.sendMessage(color("&ePerhaps try using &6get &eas an argument."));
                return false;
            };

            p.sendMessage(color("&cYou may not do this!"));
            return false;
        };
    };

    @Override public final void onDisable()
    {
        getServer().getScheduler().cancelTasks(plugin);
        print("I am dead!");
    };

    protected final String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };

    protected final void print(final String d)
    {
        System.out.println(d);
    };
};