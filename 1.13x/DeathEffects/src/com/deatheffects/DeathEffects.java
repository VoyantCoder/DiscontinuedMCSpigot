

// Author: Dashie
// Version: 1.0


package com.deatheffects;


import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class DeathEffects extends JavaPlugin implements Listener, CommandExecutor
{
    private String color(String str) { return ChatColor.translateAlternateColorCodes('&', str); };
    private void print(String str) { System.out.println("(Death Events): " + str); };
    
    private FileConfiguration config;
    private JavaPlugin plugin;
    
    @Override public void onEnable()
    {
        print("The plugin is loading ....");
        
        saveDefaultConfig();
        
        config = (FileConfiguration) getConfig();
        plugin = (JavaPlugin) this;
        
        load();
        
        getServer().getPluginManager().registerEvents(this, plugin);
        getCommand("dashiesdeath").setExecutor(this);
        
        print("The plugin has been enabled.");
    };
    
    private List<PotionEffect> potion_effects = new ArrayList<>();
    private String effect_permission, admin_permission;
    
    @EventHandler public void onPlayerDeathEvent(PlayerDeathEvent e)
    {
        if(!(e.getEntity().getKiller() instanceof Player) || e.getEntity().getKiller() == e.getEntity())
        {
            return;
        };
        
        Player klr = (Player) e.getEntity().getKiller();
        
        if (!klr.hasPermission(effect_permission))
        {
            return;
        };
        
        klr.addPotionEffects(potion_effects);
        klr.sendMessage(color("&eYou have been given a boost for killing " + e.getEntity().getName() + "!"));
    };
    
    private boolean isInteger(String str) { try { Integer.parseInt(str); return true; } catch (NumberFormatException e) { return false; } };
    private boolean isPotionEffectType(String str) { try { PotionEffectType.getByName(str); return true; } catch (NumberFormatException e) { return false; } };
    
    private void load()
    {
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        effect_permission = config.getString("properties.effect-permission");
        admin_permission = config.getString("properties.admin-permission");
        
        for (String str : config.getStringList("properties.effect-list"))
        {
            String[] arr = str.split(" ");
            
            if(arr.length < 3 || arr.length > 3 || !isPotionEffectType(arr[0]) || !isInteger(arr[1]) || !isInteger(arr[2]))
            {
                print("Invalid potion value {" + str + "} received, skipping!");
                continue;
            };
            
            potion_effects.add
            (
                new PotionEffect
                (
                    PotionEffectType.getByName(arr[0].toUpperCase()),
                    Integer.parseInt(arr[1]) * 20, Integer.parseInt(arr[2])
                )
            );
        };
    };
    
    @Override public boolean onCommand(CommandSender s, Command c, String a, String[] as)
    {
        if(!(s instanceof Player))
        {
            print("Only in-game players with the right permission(s) may use this command.");
            return false;
        };
        
        Player p = (Player) s;
        
        if(as.length >= 1 && as[0].equalsIgnoreCase("reload") && p.hasPermission(admin_permission))
        {
            p.sendMessage(color("&e&l>>> &a&lReloading ...."));
            
            load();
             
            p.sendMessage(color("&e&l>>> &a&lDone!"));
        }
        
        else
        {
            p.sendMessage(color("&cDid you mean &4reload &cor are you just prohibited from using this?"));
        };
        
        return true;
    };
    
    @Override public void onDisable()
    {
        print("The plugin has been disabled.");
    };
};

