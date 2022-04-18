
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class SkyDrops extends JavaPlugin
{
    protected FileConfiguration config;
    protected JavaPlugin plugin = this;

    protected int interval = 0, drops = 8, radius = 24, height = 50;
    protected final List<ItemStack> materials = new ArrayList<>();

    protected final class Commands implements CommandExecutor
    {
        protected final List<Player> players = new ArrayList<>();        // I know about HashMaps, I just prefer this.

        protected final void ToggleDrops(final Player p)
        {
            if (materials.size() < 1)
            {
                p.sendMessage(color("&cThe materials list is empty, please add materials and retry."));
                return;
            };

            if (players.contains(p))
            {
                p.sendMessage(color("&aYou are now no longer being rained upon!"));
                players.remove(p);
            }

            else
            {
                p.sendMessage(color("&aYou are now being rained upon!"));

                final Random rand = new Random();

                //getServer().getScheduler().scheduleSyncRepeatingTask
                //(
                //    plugin,

                    new BukkitRunnable()
                    {
                        @Override public final void run()
                        {
                            if (!p.isOnline() || !players.contains(p))
                            {
                                this.cancel();
                                return;
                            };

                            final Location location = p.getLocation();

                            for (int k = 0; k < drops; k += 1)
                            {
                                int x = location.getBlockX(); //rand.nextInt(radius);

                                if (rand.nextInt(2) < 1) x += rand.nextInt(radius);
                                else x -= rand.nextInt(radius);

                                int z = location.getBlockZ();

                                if (rand.nextInt(2) < 1) z += rand.nextInt(radius);
                                else z -= rand.nextInt(radius);

                                final int i = rand.nextInt(materials.size());
                                final int y = location.getBlockY() + height;

                                location.getWorld().dropItemNaturally(new Location(location.getWorld(), x, y, z), materials.get(i));
                            };
                        }
                    }.runTaskTimer(plugin, 0, interval * 20);

                players.add(p);
            };
        };

        @Override public final boolean onCommand(final CommandSender s, final Command c, String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only do this as a player.");
                return false;
            };

            final Player p = (Player) s;

            if (p.hasPermission("skydrops.toggle"))
            {
                if (a.equals("toggle") || a.equals("t"))
                {
                    ToggleDrops(p);
                    return true;
                };
            }

            else if (p.isOp())
            {
                if (as.length > 0)
                {
                    a = as[0];

                    if (a.equals("reload") || a.equals("r"))
                    {
                        p.sendMessage(color("&aDone!")); ReloadData();
                        return true;
                    };
                };

                p.sendMessage(color("&cDid you perhaps mean &7/skydrops [toggle | reload] &c?"));
                return true;
            };

            p.sendMessage(color("&cYou may not do this!"));
            return false;
        };
    };

    @Override public final void onEnable()
    {
        print("Loading ....");

        getCommand("skydrops").setExecutor(new Commands());
        ReloadData();

        print("Done!");
    };

    protected final void ReloadData()
    {
        saveDefaultConfig();

        plugin.reloadConfig();
        config = plugin.getConfig();

        try
        {
            materials.clear();

            try
            {
                interval = config.getInt("drop-interval");
                height = config.getInt("drop-height");
                radius = config.getInt("drop-radius");
                drops = config.getInt("drops-per-interval");

                for (int k = 1 ;; k += 1)
                {
                    if (config.contains("drop-materials." + k))
                    {
                        final String node = "drop-materials." + k + ".";

                        try
                        {
                            final Material mate = Material.valueOf(config.getString(node + "type").toUpperCase());
                            final int qua = config.getInt(node + "quantity");

                            final ItemStack material = new ItemStack(mate, qua);
                            final ItemMeta mate_meta = material.getItemMeta();

                            mate_meta.setDisplayName(color(config.getString(node + "name")));

                            final List<String> lore = new ArrayList<>();

                            for (final String l : config.getStringList(node + "lore"))
                            {
                                lore.add(color(l));
                            };

                            mate_meta.setLore(lore);

                            material.setItemMeta(mate_meta);
                            materials.add(material);
                        }

                        catch (final Exception e)
                        {
                            print("Invalid item-data was found at " + node + "^ !");
                        };

                        continue;
                    };

                    break;
                };
            }

            catch (final Exception e)
            {
                print("An error has occurred while trying to load integral values from the config.  All integral variables have been set to their default values.");

                interval = 8;
                height = 50;
                radius = 24;
                drops = 8;
            };
        }

        catch (final Exception e)
        {
            print("An error has occurred while loading configuration data.  Please contact me at KvinneKraft@protonmail.com if this issue occurs again.");
        };
    };

    @Override public final void onDisable()
    {    };

    protected final String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };

    protected final void print(final String d)
    {
        System.out.println("(Sky Drops): " + d);
    };
};