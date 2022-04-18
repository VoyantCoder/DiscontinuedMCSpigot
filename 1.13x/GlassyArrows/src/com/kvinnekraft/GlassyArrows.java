
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GlassyArrows extends JavaPlugin
{
    FileConfiguration config;
    JavaPlugin plugin;
    
    @Override public void onEnable()
    {
        print("I am crawling out of my tomb ....");
        
        LoadConfiguration();
        
        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("glassyarrows").setExecutor(new Commands());
        
        print
        (
            "\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\n" +
            " Author: Dashie\n" +
            " Version: 1.0\n" +
            "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\n" +
            " Email: KvinneKraft@protonmail.com\n" +
            " Github: https://github.com/KvinneKraft \n" +
            "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\n"
        );
        
        print("Guess what I did? I crawled out of my tomb!");
    };
    
    final List<EntityType> entity_types = new ArrayList<>();
    final List<Material> glass_types = new ArrayList<>();    
    
    String break_permission, admin_permission;
    int break_chance, break_radius;
    
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
            break_permission = config.getString("permissions.glass-break-permission");
            admin_permission = config.getString("permissions.plugin-admin-permission");            
            
            try
            {
                break_chance = config.getInt("glassy-arrows.arrow-break-chance");
                break_radius = config.getInt("glassy-arrows.arrow-break-radius") - 1;
                
                if (break_radius < 0)
                {
                    break_radius = 0;
                };
            }
            
            catch (final Exception e)
            {
                print("One or more none integral value(s) had been found in the configuration file!");
                
                break_chance = 50;
                break_radius = 1;
            };
            
            entity_types.clear();
            glass_types.clear();            
            
            String[] paths = { "considerable-arrows", "considerable-glass" };
            
            for (final String path : paths)
            {
                for (final String data : config.getStringList("glassy-arrows." + path))
                {
                    try
                    {   
                        if (path.equals(paths[0]))
                        {
                            final EntityType entity_type = EntityType.valueOf(data);
                            
                            if (entity_type == null)
                            {
                                throw new Exception("ERROR");
                            };
                            
                            entity_types.add(entity_type);
                        }

                        else
                        {
                            final Material material = Material.valueOf(data);

                            if (material == null)
                            {
                                throw new Exception("ERROR");
                            };                            
                            
                            glass_types.add(material);
                        };
                    }
                    
                    catch (final Exception e)
                    {
                        print("Found an invalid material in the configuration file under the flaming-arrows section! Skipping....");
                    };
                };
            };
        }
        
        catch (final Exception e)
        {
            print("An unknown error had occurred in the configuration file!");
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
            
            if (!p.hasPermission(admin_permission))
            {
                p.sendMessage(color("&cYou may not do this!"));
                return false;
            };
            
            if (as.length >= 1)
            {
                if (as[0].equalsIgnoreCase("reload"))
                {
                    p.sendMessage(color("&a> Reloading ...."));
                    
                    LoadConfiguration();
                    
                    p.sendMessage(color("&a> Done!"));
                    
                    return true;
                };
            };
            
            p.sendMessage(color("&cInvalid syntax, valid syntax: &4&o/glassyarrows reload"));
            
            return true;
        };
    };
    
    protected class Events implements Listener
    {
        @EventHandler public void onProjectileHit(final ProjectileHitEvent e)
        {
            if (e.getEntity().getShooter() instanceof Player)
            {            
                final Player p = (Player) e.getEntity().getShooter();

                if (p.hasPermission(break_permission))
                {
                    final Entity entity = (Entity) e.getEntity();

                    if (entity_types.contains(entity.getType()))
                    {
                        if (e.getHitBlock() != null && glass_types.contains(e.getHitBlock().getType()))
                        {
                            final Random rand = new Random();

                            if (rand.nextInt(100) < break_chance)
                            {
                                getServer().getScheduler().runTaskAsynchronously
                                (
                                    plugin, 
                                        
                                    new Runnable() 
                                    { 
                                        @Override public void run() 
                                        {
                                            final Location location = e.getHitBlock().getLocation();
                                            
                                            for (int x = location.getBlockX() - break_radius; x <= location.getBlockX() + break_radius; x++)
                                            {
                                                for (int y = location.getBlockY() - break_radius; y <= location.getBlockY() + break_radius; y++)
                                                {
                                                    for (int z = location.getBlockZ() - break_radius; z <= location.getBlockZ() + break_radius; z++)
                                                    {
                                                        final Block block = (Block) location.getWorld().getBlockAt(x, y, z);
                                                        
                                                        if (glass_types.contains(block.getType()))
                                                        {
                                                            getServer().getScheduler().runTask
                                                            (
                                                                plugin,
                                                                    
                                                                new Runnable()
                                                                {
                                                                    @Override public void run()
                                                                    {
                                                                        entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_GLASS_BREAK, 10, 10);                                                                            
                                                                        block.breakNaturally();
                                                                    };
                                                                }
                                                            );
                                                        };
                                                    };
                                                };
                                            };
                                            
                                            getServer().getScheduler().runTask
                                            (
                                                plugin, 
                                                    
                                                new Runnable() 
                                                { 
                                                    @Override public void run() 
                                                    {                            
                                                        entity.remove();                                                         
                                                    }; 
                                                }
                                            );
                                        }; 
                                    }
                                );
                            };
                        };
                    };
                };
            };
        };
    };
    
    @Override public void onDisable()
    {
        print("I am dead now.");
    };
    
    protected String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };
    
    protected void print(final String d)
    {
        System.out.println("(Glassy Arrows): " + d);
    };
};