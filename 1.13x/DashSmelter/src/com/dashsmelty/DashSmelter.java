
// Author: Dashie
// Version: 1.0

package com.dashsmelty;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DashSmelter extends JavaPlugin
{
    public static FileConfiguration config;
    public static JavaPlugin plugin;
    public static Furnace furnace;

    @Override
    public void onEnable()
    {
        Moon.Print("The plugin is currently being initialized ....");
        
        saveDefaultConfig();
        
        config = (FileConfiguration)this.getConfig();
        plugin = this;
        
        furnace = new Furnace();
        furnace.RegisterRecipes();
        
        getCommand("dashsmelt").setExecutor(new CommandsHandler());
        
        Moon.Print("The plugin is now running!");
    };
    
    @Override
    public void onDisable()
    {
        Moon.Print("The plugin has been disabled.");
    };
};
