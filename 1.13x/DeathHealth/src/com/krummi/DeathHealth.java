

// Author: Dashie
// Version: 1.0


package com.krummi;


import org.bukkit.ChatColor;
//import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class DeathHealth extends JavaPlugin
{
    //public static FileConfiguration config;
    public static JavaPlugin plugin;
    
    public static Events events = new Events();
    
    @Override public void onEnable() 
    {
        KvinneKraft.print("Plugin is being enabled ....");

        //zconfig = (FileConfiguration) getConfig();
        plugin = (JavaPlugin) this;
        
        getServer().getPluginManager().registerEvents(events, plugin);
        
        KvinneKraft.print("Author: Dashie");
        KvinneKraft.print("Version: 1.0");
        KvinneKraft.print("Email: KvinneKraft@protonmail.com");
        KvinneKraft.print("Github: https://github.com/KvinneKraft/DashSociety");       
        
        KvinneKraft.print("Plugin has been enabled!");        
    };
    
    @Override public void onDisable()
    {
        KvinneKraft.print("Plugin has been disabled!");
    };
};

class KvinneKraft
{
    static void print(String str)
    {
        System.out.println("(Death Health): " + str);
    };
        
    static String color(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
};
