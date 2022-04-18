
// Author: Dashie
// Version: 1.0

package com.dashcare;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Heal extends JavaPlugin implements CommandExecutor
{
    private String color(String str) { return ChatColor.translateAlternateColorCodes('&', str); } ;
    private void print(String str) { System.out.println("(Dash Care): " + str); } ;
    
    private FileConfiguration config;
    private JavaPlugin plugin;
    
    @Override public void onEnable()
    {
        print("Initializing Dash Care ....");
        
        saveDefaultConfig();
        
        config = (FileConfiguration) getConfig();
        plugin = (JavaPlugin) this;
        
        getCommand("heal").setExecutor(this);  
        LoadConfiguration();
        
        print("Dash Care is now up and running!");
    };
    
    private String command_permission, cooldown_message, heal_message;
    private int cooldown;
    
    private void LoadConfiguration()
    {
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        command_permission = config.getString("use-command-permission");
        cooldown_message = color(config.getString("cooldown-message"));
        heal_message = color(config.getString("heal-message"));
        cooldown = config.getInt("cooldown") * 20;
    };
    
    private final List<Player> pdb = new ArrayList<>();
    
    @Override public boolean onCommand(CommandSender s, Command c, String a, String[] as)
    {
        if(!(s instanceof Player))
        {
            print("Command sender must be a player!");
            return false;
        };
        
        Player p = (Player) s;
        
        if(pdb.contains(p) && !p.isOp())
        {
            p.sendMessage(cooldown_message);
            return false;
        };
        
        if(!p.hasPermission(command_permission))
        {
            p.sendMessage(color("&cYou may not use this command!"));
            return false;
        };
        
        p.setHealth(p.getMaxHealth());
        p.sendMessage(heal_message);
        
        if(!p.isOp())
        {
            getServer().getScheduler().runTaskLater
            (
                plugin, 
                
                new Runnable() 
                { 
                    @Override public void run() 
                    {
                        if (pdb.contains(p))
                        {
                            pdb.remove(p);
                        };
                        
                        if(p.isOnline())
                        {
                            p.sendMessage(color("&aYou may now use &e/heal &aagain!"));
                        };
                    }; 
                }, 
                
                cooldown
            );
        };
        
        return true;
    };
    
    @Override public void onDisable()
    {
        print("Dash Care has been disabled!");
    };
};