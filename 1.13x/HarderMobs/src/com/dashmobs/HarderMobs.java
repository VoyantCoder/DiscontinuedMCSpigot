

// Author: Dashie
// Version: 1.0


package com.dashmobs;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;


public class HarderMobs extends JavaPlugin
{
    public static FileConfiguration config;
    public static JavaPlugin plugin;
    
    public static Commands commands = new Commands();
    public static Events events = new Events();
    
    
    @Override
    public void onEnable()
    {
        print("Plugin is being enabled ....");
        
        config = (FileConfiguration) getConfig();
        plugin = (JavaPlugin) this;
        
        DashManager.reload_plugin();
        
        getServer().getPluginManager().registerEvents(events, plugin);
        getCommand("hardermobs").setExecutor(commands);
        
        print("Author: Dashie");
        print("Version: 1.0");
        print("---------------");
        print("Github: https://github.com/KvinneKraft/Dashnarok/");
        print("Website: https://pugpawz.com/");
        print("Email: KvinneKraft@protonmail.com");
        
        print("Plugin has been enabled!");
    };
    
    
    @Override
    public void onDisable()
    {
        print("Plugin has been disabled!");
    };
    
    
    public static void print(String str)
    {
        System.out.println("(Harder Mobs): " + str);
    };
    
    
    public static String color(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
};
