
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class AngryBlocks extends JavaPlugin
{
    @Override public void onEnable()
    {
        print("Loading .....");

        LoadConfiguration();

        print(" Author: Dashie");
        print(" Name: Angry Blocks");
        print(" Email: KvinneKraft@protonmail.com");
        print(" Github: https://github.com/KvinneKraft");

        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("angryblocks").setExecutor(new Commands());

        print("Now up and running!");
    };

    FileConfiguration config;
    JavaPlugin plugin;

    protected final void LoadConfiguration()
    {
        if (plugin != this)
        {
            plugin = this;
        };

        saveDefaultConfig();

        plugin.reloadConfig();
        config = plugin.getConfig();

        try
        {
            bypass = config.getString("bypass-permission");
            admin  = config.getString("reload-permission");

            block_types.clear();

            for (final String data : config.getStringList("angry-block-selection"))
            {
                try
                {
                    final Material material = Material.valueOf(data.toUpperCase().replace(" ", "_"));
                    block_types.add(material.toString());
                }

                catch (final Exception e)
                {
                    print("ERROR! The block " + data + " is not recognized, skipping ....");
                };
            };

            world_names.clear();

            for (final String data : config.getStringList("prohibited-worlds"))
            {
                try
                {
                    final World world = getServer().getWorld(data);

                    if (world == null)
                    {
                        throw new Exception("e");
                    };

                    world_names.add(world.getName());
                }

                catch (final Exception e)
                {
                    print("ERROR! The world " + data + " is invalid, skipping ....");
                };
            };
        }

        catch (final Exception e)
        {
            print("An unknown error has occurred, please contact me at KvinneKraft@protonmail.com if this error persists.");
        };
    };

    final List<String> world_names = new ArrayList<>();
    final List<String> block_types = new ArrayList<>();

    String bypass, admin;

    protected final class Events implements Listener
    {
        @EventHandler protected final void onPlayerMovement(final PlayerMoveEvent e)
        {
            final Player p = e.getPlayer();

            if (!p.hasPermission(bypass) && !p.hasPermission(admin))
            {
                if (!world_names.contains(p.getWorld().getName()))
                {
                    final Location l = p.getLocation();

                    if (block_types.contains(new Location(l.getWorld(), (int) l.getX(), (int) l.getY() - 1, (int) l.getZ()).getBlock().getType().toString()))
                    {
                        p.sendMessage(color("Hey"));
                        p.setHealth(0);
                    };
                };
            };
        };
    };

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

            if (p.hasPermission(admin))
            {
                if (as.length > 0 && as[0].equalsIgnoreCase("reload"))
                {
                    LoadConfiguration();
                    p.sendMessage(color("&aDone!"));
                    return true;
                };

                p.sendMessage(color("&cPerhaps try &7/angryblocks reload &c!"));
                return false;
            };

            p.sendMessage(color("&cYou may not do this!"));
            return false;
        };
    };

    @Override public void onDisable()
    {
        print("The plugin is now dead.");
    };

    protected final void print(final String data)
    {
        System.out.println("(Angry Blocks): " + data);
    };

    protected final String color(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    };
};