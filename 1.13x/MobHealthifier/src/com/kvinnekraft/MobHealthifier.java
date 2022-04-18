
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MobHealthifier extends JavaPlugin
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
            admin_permission = config.getString("admin-permission");
            
            organisms.clear();
            healths.clear();
            
            for (final String data : config.getStringList("modifiers"))
            {
                try
                {
                    final String[] arr = data.split(" ");

                    if (arr.length < 2)
                    {
                        throw new Exception("ERROR");
                    };
                    
                    final EntityType entity = EntityType.valueOf(arr[0].toUpperCase());                    
                    organisms.add(entity);                    
                    
                    if (entity == null)
                    {
                        throw new Exception("ERROR");
                    };
                    
                    final double health = Double.valueOf(arr[1]);
                    healths.add(health);
                }
                
                catch (final Exception e)
                {
                    print("An invalid modifier had been found and will be skipped ....");
                };
            };
            
            modify_chance = config.getInt("modify-chance");
        }
        
        catch (final Exception e)
        {
            print("An unknown error had occurred while reading from the configuration file!");
            print("Please contact me at KvinneKraft@protonmail.com.");
        };
    };
    
    @Override public void onEnable()
    {
        print("I am swimming to the surface ....");
        
        LoadConfiguration();        
        
        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("mobhealthifier").setExecutor(new Commands());
        
        print("I am at the surface!");
    };
    
    final List<EntityType> organisms = new ArrayList<>();
    final List<Double> healths = new ArrayList<>();
    
    Integer modify_chance;
    
    protected class Events implements Listener
    {
        @EventHandler public void onCreatureSpawn(final EntitySpawnEvent e)
        {
            final Entity entity = e.getEntity();
            
            if (entity instanceof LivingEntity)
            {
                if (organisms.contains(entity.getType()))
                {
                    if (new Random().nextInt(1000) <= modify_chance)
                    {
                        final LivingEntity lively = (LivingEntity) entity;                    
                    
                        lively.setMaxHealth(healths.get(organisms.indexOf(entity.getType())));
                        lively.setHealth(healths.get(organisms.indexOf(entity.getType())));
                    };
                };
            };
        };
    };
    
    String admin_permission;

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
                    p.sendMessage(color("&a> Processing ...."));

                    LoadConfiguration();

                    p.sendMessage(color("&a> Done!"));

                    return true;
                };
            };

            p.sendMessage(color("&cInvalid syntax, did you mean: &4&o/healthifier reload"));
            return true;
        };
    };
    
    @Override public void onDisable()
    {
        print("I drowned.");
    };
    
    protected String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };
    
    protected void print(final String d)
    {
        System.out.println("(Mob Healthier): " + d);
    };
};