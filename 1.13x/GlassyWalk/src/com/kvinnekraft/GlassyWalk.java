
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GlassyWalk extends JavaPlugin
{	
    FileConfiguration config;
    JavaPlugin plugin;

    @Override public void onEnable()
    {
        print("I am crawling out of my grave ....");

        LoadConfiguration();

        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("glassywalk").setExecutor(new Commands());
        
        print
        (
            (
                "\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- \n" +
                " Author: Dashie                           \n" +
                " Version: 1.0                             \n" +
                " Email: KvinneKraft@protonmail.com        \n" +
                " Github: https://github.com/KvinneKraft/  \n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-   "
            )
        );

        print("I am alive!");
    };

    protected void LoadConfiguration()
    {
        if (plugin != this)
        {
            plugin = (JavaPlugin) this;
        };

        saveDefaultConfig();

        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        try
        {
            admin_permission = config.getString("permissions.to-reload-plugin");
            break_permission = config.getString("permissions.to-break-blocks");
            
            breakable_blocks.clear();
            
            for (final String data : config.getStringList("glassy-walk.the-breakable-blocks"))
            {
                try
                {
                    final Material substance = Material.valueOf(data);
                    
                    if (substance == null)
                    {
                        throw new Exception("ERROR");
                    };
                    
                    breakable_blocks.add(substance);
                }
                
                catch (final Exception e)
                {
                    print("Hey, I found an inproper material in the configuration file. the-breakable-blocks: [" + data + "]");
                };
            };
            
            break_chance = config.getInt("glassy-walk.the-break-chance");            
        }
        
        catch (final Exception e)
        {
            print("An unknown configuration error had occurred, please contact me!");
        };
    };
    
    final List<Material> breakable_blocks = new ArrayList<>();
    final Random rand = new Random();
    
    int break_chance;
    
    protected class Events implements Listener
    {
        @EventHandler public void onPlayerMovement(final PlayerMoveEvent e)
        {
            final Player p = (Player) e.getPlayer();
            
            if (p.hasPermission(break_permission))
            {
                getServer().getScheduler().runTaskAsynchronously
                (
                    plugin,
                    
                    new Runnable()
                    {
                        @Override public void run()
                        {
                            final World world = (World) p.getWorld();
                            
                            final double x = p.getLocation().getBlockX();
                            final double y = p.getLocation().getBlockY() - 1;
                            final double z = p.getLocation().getBlockZ();
                            
                            final Location location = new Location(world, x, y, z);
                            
                            if ((breakable_blocks.contains(location.getBlock().getType())))
                            {
                                if (rand.nextInt(1000) < break_chance)
                                {
                                    getServer().getScheduler().runTask
                                    (
                                        plugin,
                                            
                                        new Runnable()
                                        {
                                            @Override public void run()
                                            {
                                                world.getBlockAt(location).breakNaturally();
                                            };
                                        }
                                    );
                                };
                            };                            
                        };
                    }
                );
            };
        };
    };
    
    String admin_permission, break_permission;

    protected class Commands implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only do this as a player!");
                return false;
            };

            final Player p = (Player) s;

            if (!p.hasPermission(admin_permission))
            {
                p.sendMessage(color("&cYou may not do this!"));
                return false;
            };

            if (as.length >= 1)
            {
                a = as[0].toLowerCase();

                if (a.equals("reload"))
                {
                    p.sendMessage(color("&a> Loading ...."));

                    LoadConfiguration();

                    p.sendMessage(color("&a> Done!"));

                    return true;
                };
            };

            p.sendMessage(color("&cInvalid syntax, did you mean: &4&o/glassywalk reload"));
            return true;
        };
    };

    @Override public void onDisable()
    {
        print("I am dead!");
    };

    protected String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };

    protected void print(final String d)
    {
        System.out.println("(Glassy Walk): " + d);
    };
};