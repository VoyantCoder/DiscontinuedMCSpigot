
// Author: Dashie
// Version: 1.0

package com.dash.security;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DashSecurity extends JavaPlugin
{
    public static FileConfiguration config;
    public static JavaPlugin plugin;
    
    public static CommandHandler commandhandler = new CommandHandler();    
    public static EventListener listener = new EventListener();
    
    
    @Override public void onEnable()
    {
        DashUtility.print("Plugin is loading ....");
        
        saveDefaultConfig();
        
        config = (FileConfiguration) getConfig();
        plugin = (JavaPlugin) this;
        
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        plugin.getCommand("dashsecurity").setExecutor(commandhandler);
        
        DashUtility.print("Plugin has been loaded!");
    };
    
    
    @Override public void onDisable()
    {
        DashUtility.print("Plugin has been closed!");
    };
};