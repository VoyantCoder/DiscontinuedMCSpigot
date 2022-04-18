
// Custom Join plugin for a friend of mine.

// Author: Dashie
// Version: 1.0

package com.dashjoin;

import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Moony extends JavaPlugin implements Listener, CommandExecutor
{
    FileConfiguration config = getConfig();
    JavaPlugin plugin = this;
    
    Moon luna = new Moon();
    
    private void loadData()
    {
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        bypass_permission = config.getString("bypass-permission");
        
        join_message = luna.transstr(config.getString("messages.join"));
        quit_message = luna.transstr(config.getString("messages.quit"));
    };
    
    @Override
    public void onEnable()
    {
        luna.print("Loading ....");
        
        saveDefaultConfig();
        
        loadData();
        
        getServer().getPluginManager().registerEvents(this, plugin);
        getCommand("dashjoin").setExecutor(this);
        
        luna.print("Author: Dashie");
        luna.print("Version: 1.0");
        
        luna.print("Done loading!");
    };
    
    String join_message, quit_message, bypass_permission;
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        
        if(p.hasPermission(bypass_permission))
        {
            e.setJoinMessage(null);
        }
        
        else
        {
            e.setJoinMessage(join_message.replace("%p%", p.getName()));
        };
    };
    
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        
        if(p.hasPermission(bypass_permission))
        {
            e.setQuitMessage(null);
        }
        
        else
        {
            e.setQuitMessage(quit_message.replace("%p%", p.getName()));
        };
    };
    
    boolean t = true, f = false;
    
    String denied_message = luna.transstr("&cYou are not supposed to do this, huh?");
    
    String reloading_message = luna.transstr("&aReloading the Dash Join ....");
    String reloaded_message = luna.transstr("&aDash Join has been reloaded.");
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String a, String[] as)
    {
        if(!(s instanceof Player))
            return f;
        
        Player p = (Player) s;
        
        if(p.isOp())
        {
            p.sendMessage(reloading_message);
            
            loadData();
            
            p.sendMessage(reloaded_message);
        }
        
        else
        {
            p.sendMessage(denied_message);
        };
        
        return t;
    };  
    
    @Override
    public void onDisable()
    {
        luna.print("The plugin has been disabled.");
    };
    
    class Moon 
    {
        public void print(String str)
        {
            System.out.println("(Dash Join): " + str);
        };
        
        public String transstr(String str)
        {
            return ChatColor.translateAlternateColorCodes('&', str);
        };
    };
};