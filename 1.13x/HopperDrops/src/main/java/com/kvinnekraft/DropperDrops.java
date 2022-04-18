
// Ugly Code....I know, it is just for experimentation purposes only.
// I barely have any time for this anyway.

// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DropperDrops extends JavaPlugin
{
    final FileConfiguration config = this.getConfig();

    protected final class DropperBlock
    {
        protected final ItemStack Dropper = new ItemStack(Material.DROPPER, 1);

        public DropperBlock()
        {
            final ItemMeta DropperMeta = Dropper.getItemMeta();

            DropperMeta.setDisplayName(color("&bDropper Block"));
            DropperMeta.setLore(Arrays.asList(color("&ePut me down for use!")));
            DropperMeta.setCustomModelData(2020);

            Dropper.setItemMeta(DropperMeta);
        };
    };

    DropperBlock DropperBlock = new DropperBlock();

    protected final class Events implements Listener
    {
        @EventHandler public final void onBlockPowered(final BlockRedstoneEvent e)
        {
            final Block block = e.getBlock();

            print("3");

            //if (locations.contains(block.getLocation()))
            //{
                //if (block.getType().equals(Material.HOPPER))
                //{
                    // Just randomly pick an item from a list.
                    block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.STICK, 1));
                //};
            //};
        };

        @EventHandler public final void onBlockBreak(final BlockBreakEvent e)
        {
            final Block block = e.getBlock();

            if (locations.contains(block.getLocation()))
            {
                print("2");

                //if (block.getType().equals(Material.HOPPER))
                //{
                    print("2");

                    locations.remove(block.getLocation());
                //};
            };
        };

        @EventHandler public final void onBlockPlace(final BlockPlaceEvent e)
        {
            final Player p = e.getPlayer();

            if (p.getInventory().getItemInMainHand().isSimilar(DropperBlock.Dropper))
            {
                locations.add(e.getBlock().getLocation());
            };
        };
    };

    protected final List<Location> locations = new ArrayList<>();

    protected final class Commands implements CommandExecutor
    {
        @Override public final boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only do this as a player!");
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

                            catch (final Exception e) // Why did Java not make this simpler?
                            {
                                p.sendMessage(color("&cThe amount must be an integral value!"));
                                return false;
                            };
                        };

                        DropperBlock.Dropper.setAmount(amount);

                        p.getInventory().addItem(DropperBlock.Dropper);
                        p.sendMessage(color("&aYou have given yourself the power of Droppers!"));

                        return true;
                    };
                };

                p.sendMessage(color("&aDid you perhaps mean &7/dropperdrops get [amount] &a?"));
                return false;
            };

            p.sendMessage(color("&cYou may not do this!"));
            return false;
        };
    };

    @Override public final void onEnable()
    {
        print("I am being enabled...");

        saveDefaultConfig();

        if (config.contains("block-locations"))
        {
            locations.addAll((List<Location>) config.get("block-locations"));
        };

        getServer().getPluginManager().registerEvents(new Events(), this);
        getCommand("dropperdrops").setExecutor(new Commands());

        print("I have been enabled!");
    };

    protected final String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };

    protected final void print(final String d)
    {
        System.out.println("(Dropper Drops): " + d);
    };

    @Override public final void onDisable()
    {
        config.set("block-locations", locations); this.saveConfig();
        print("Ouch, you have disabled me!");
    };
};