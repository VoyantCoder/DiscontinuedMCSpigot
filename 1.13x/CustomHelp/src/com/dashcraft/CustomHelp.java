

// Author: Dashie
// Version: 1.0


package com.dashcraft;


import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class CustomHelp extends JavaPlugin implements Listener
{
    private FileConfiguration config;
    private JavaPlugin plugin;
    
    
    private void initializeCommand()
    {
        if(help_message.length() > 0) help_message = "";
        
        for (String str : config.getStringList("properties.help-message"))
        {
            if(help_message == "") 
            {
                help_message = str; 
                continue; 
            };
            
            help_message += help_message + "\r\n" + str;
        };
        
        help_message = color(help_message);
        
        permission_message = color(config.getString("properties.help-denied-message"));
        permission = config.getString("properties.help-permission");
    };
    
    
    @Override public void onEnable()
    {
        print("The plugin is initializing ....");
        
        saveDefaultConfig();
        
        config = (FileConfiguration) getConfig();
        plugin = (JavaPlugin) this;
        
        initializeCommand();
        
        getServer().getPluginManager().registerEvents(this, plugin);
        
        print("The plugin has been enabled!");
    };
    
    @Override public void onDisable()
    {
        print("The plugin has been disabled!");
    };
    

    private String help_message = "", permission_message, permission;
    
    
    @EventHandler public void onPlayerCommand(PlayerCommandPreprocessEvent e)
    {
        if(!e.getMessage().toLowerCase().contains("help") && !e.getMessage().toLowerCase().contains("info"))
        {
            return;
        };
        
        Player p = (Player) e.getPlayer();
        
        if(!p.hasPermission(permission))
        {
            p.sendMessage(permission_message);
            e.setCancelled(true);
            
            return;
        };
        
        p.sendMessage(help_message);
        e.setCancelled(true);
    };
    
    
    public static void print(String str)
    {
        System.out.println("(Custom Help): " + str);
    };
    
    public static String color(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
};
