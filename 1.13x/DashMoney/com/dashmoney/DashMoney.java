
// Author: Dashie
// Version: 1.0

package com.dashmoney;


import com.dashmoney.events.EventHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class DashMoney extends JavaPlugin
{
    public static FileConfiguration config;
    public static JavaPlugin plugin;
    
    String Author = "Author: Dashie";
    String PluginName = "Plugin Name: Dash Money";
    String Version = "Version: 1.0";
    
    public static Economy econ;
    
    @Override
    public void onEnable()
    {
        Moon.print("Initializing plugin ....");
        
        saveDefaultConfig();
        
        config = getConfig();
        plugin = (JavaPlugin)this;
        
        Moon.print(Author);
        Moon.print(PluginName);
        Moon.print(Version);
        
        if(!Moon.hasVault())
        {
            Moon.print("Vault could not be found on your server ;c");
            getServer().getPluginManager().disablePlugin(plugin);
            return;
        };
        
        econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();        
        
        getServer().getPluginManager().registerEvents(new EventHandler(), plugin);
        
        Moon.print("Plugin is now running!");
    };
    
    @Override
    public void onDisable()
    {
        Moon.print("The plugin has been disabled!");
    };
};
