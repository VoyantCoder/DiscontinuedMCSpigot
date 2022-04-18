
// Author: Dashie
// Version: 1.0

package com.DashJoin;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.ArrayList;
import org.bukkit.Sound;
import java.util.List;


public class Sounds extends JavaPlugin implements Listener
{
    FileConfiguration config;// = getConfig();
    Events events;
    
    @Override
    public void onEnable()
    {
        Kvinne.print("Loading the plugin ....");
        
        saveDefaultConfig();
        
        config = (FileConfiguration) getConfig();
        events = new Events();
        
        getServer().getPluginManager().registerEvents(events, this);
        
        Kvinne.print("Done loading the plugin!");
    };
    
    
    class Events implements Listener
    {
        List<Player> players = new ArrayList<>();


        Sound sound = Sound.valueOf(config.getString("sound_effect").replace(".", "_").replace(" ", "_").toUpperCase());    


        String trigger_permission = config.getString("trigger-permission-node");    
        String notify_permission = config.getString("notify-permission-node");


        @EventHandler
        void onPlayerConnect(PlayerJoinEvent e)
        {
            Player p = e.getPlayer();

            if(p.hasPermission(notify_permission))
            {
                if(!players.contains(p))
                {
                    players.add(p);
                };
            };        

            if(p.hasPermission(trigger_permission))
            {
                if(players.size() > 0)
                {
                    for(Player sp : players)
                    {
                        if((sp.isOnline()) && (sp != p))
                        {
                            sp.playSound(sp.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 30, 30);
                        };
                    };
                };        
            };

            return;
        };


        @EventHandler
        void onPlayerDisconnect(PlayerQuitEvent e)
        {
            Player p = e.getPlayer();

            if(players.contains(p))
            {
                players.remove(p);
            };

            return;
        };
    };
    
    
    @Override
    public void onDisable()
    {
        Kvinne.print("The plugin has been disabled!");
    };
};



class Kvinne
{
    public static void print(String str)
    {
        System.out.println("(Dashies Join Sounds): " + str);
    };
    
    
    public static String color(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
};
