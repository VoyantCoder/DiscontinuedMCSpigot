
// Author: Dashie
// Version: 1.0

package com.dashcollection;


import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


public class Session extends JavaPlugin
{
    static FileConfiguration config;
    static JavaPlugin plugin;  
    
    static CommandsHandler commands;    
    static EventsHandler events; 
    static Moony moon;      
    
    @Override
    public void onEnable()
    {
        moon = new Moony();        
        
        moon.print("Loading plugin ....");        
        
        server = getServer();        
        plugin = this;
        
        saveDefaultConfig();    
        
        commands = new CommandsHandler();
        events = new EventsHandler();          

        refreshDashData();      
        
        if(server.getOnlinePlayers().size() > 0)
        {
            for(Player p : server.getOnlinePlayers())
            {
                events.add_reward_task(p, p.getUniqueId());
            };
        };
        
        getServer().getPluginManager().registerEvents(events, plugin);
        getCommand("dashsession").setExecutor(commands);
        
        moon.print("Plugin has been loaded!");
    };
    
    static Server server;
    
    public static void refreshDashData()
    {     
        plugin.reloadConfig();
        config = plugin.getConfig();        
        
        commands.Refresh();
        events.Refresh();
    };
    
    @Override
    public void onDisable()
    {
        if(events != null)
        {
            events.suspend_all_threads();
        };
        
        moon.print("Plugin has been disabled!");
    };
    
    
    class Moony
    {
        public void print(String str)
        {
            System.out.println("(Dash Session): " + str);
        };
        
        public String transStr(String str)
        {
            return ChatColor.translateAlternateColorCodes('&', str);
        };
    };
};
