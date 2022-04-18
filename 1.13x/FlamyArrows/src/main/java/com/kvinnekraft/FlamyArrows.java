
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlamyArrows extends JavaPlugin
{
    FileConfiguration config;
    JavaPlugin plugin;

    protected void LoadConfiguration()
    {
        saveDefaultConfig();

        if (plugin != this)
        {
            plugin = (JavaPlugin) this;
        };

        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();

        try
        {
            burn_chance = config.getInt("flamy-arrows.burn-chance");

            burnable_blocks.clear();

            for (final String d : config.getStringList("flamy-arrows.burnable-materials"))
            {
                try
                {
                    final Material substance = Material.valueOf(d);

                    if (substance == null)
                    {
                        throw new Exception("ERROR");
                    };

                    burnable_blocks.add(substance);
                }

                catch (final Exception e)
                {
                    print("An invalid material had been found in the configuration file. Perhaps check your config.yml! Skipping ....");
                };
            };

            admin_permission = config.getString("flamy-arrows.admin-permission");
            burn_permission = config.getString("flamy-arrows.burn-permission");
        }

        catch (final Exception e)
        {
            print("An unknown error had occurred while configuring the plugin. Please contact me at KvinneKraft@protonmail.com about this.");
        };
    };

    @Override public void onEnable()
    {
        print("I am getting out of my grave ....");

        LoadConfiguration();

        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("flamyarrows").setExecutor(new Commands());

        print("\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\nAuthor: Dashie \nVersion: 1.0 \nEmail: KvinneKraft@protonmail.com \nGithub: https://github.com/KvinneKraft \n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        print("I am now alive!");
    };

    final List<Material> burnable_blocks = new ArrayList<>();

    String burn_permission, admin_permission;
    Integer burn_chance;

    protected class Events implements Listener
    {
        @EventHandler protected void onProjectileHit(final ProjectileHitEvent e)
        {
            final Entity projectile = (Entity) e.getEntity();

            if (projectile instanceof Arrow)
            {
                final Arrow arrow = (Arrow) projectile;

                if (arrow.getFireTicks() > 0 && arrow.getShooter() instanceof Player)
                {
                    final Player p = (Player) arrow.getShooter();

                    if (p.hasPermission(burn_permission))
                    {
                        final Block block = (Block) e.getHitBlock();

                        if (burnable_blocks.contains(block.getType()))
                        {
                            final Location location = (Location) block.getLocation();

                            if (new Random().nextInt(1000) < burn_chance)
                            {
                                block.getRelative(BlockFace.SELF).setType(Material.FIRE);
                                arrow.remove();
                            };
                        };
                    };
                };
            };
        };
    };

    protected class Commands implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only do this as a player!");
                return false;
            };

            final Player p = (Player) s;

            if (p.hasPermission(admin_permission))
            {
                if (as.length >= 1)
                {
                    if (as[0].equalsIgnoreCase("reload"))
                    {
                        p.sendMessage(color("&aProcessing Configuration Information ...."));

                        LoadConfiguration();

                        p.sendMessage(color("&aDone Processing!"));
                        return true;
                    };
                };

                p.sendMessage(color("&cDid you mean to type: &e&o/flamyarrows reload &c?"));
                return true;
            };

            p.sendMessage(color("&cYou may not do this!"));
            return true;
        };
    };

    @Override public void onDisable()
    {
        print("I am now dead.");
    };

    protected void print(final String d)
    {
        System.out.println("(Flamy Arrows): " + d);
    };

    protected String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };
};