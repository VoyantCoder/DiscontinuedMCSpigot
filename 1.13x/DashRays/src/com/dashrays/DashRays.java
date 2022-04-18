
package com.dashrays;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DashRays extends JavaPlugin
{
    public static FileConfiguration config;
    public static JavaPlugin plugin;
    
    public static List<String> names = new ArrayList<String>();    
    
    public static EventsHandler events_handler;
    public CommandsHandler commands_handler;
    
    @Override
    public void onEnable()
    {
        Luna.print("Initializing the Dash Rays plugin ....");
        
        saveDefaultConfig();
        
        plugin = this;        
        config = getConfig();
        
        commands_handler = new CommandsHandler();        
        events_handler = new EventsHandler();
        
        events_handler.blocks = config.getStringList("properties.blocks");            
        commands_handler.refreshData();
                
        getServer().getPluginManager().registerEvents(events_handler, this);
        getCommand("dashrays").setExecutor(commands_handler);
                
        Luna.print("The Dash Rays plugin has been loaded successfully!");
    };
    
    @Override
    public void onDisable()
    {
        Luna.print("Aw, the plugin has been disabled.");
    };
};
