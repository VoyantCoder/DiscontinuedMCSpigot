
// Author: Dashie 
// Version: 1.0

package com.dashrays;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventsHandler implements Listener
{
    FileConfiguration config = Luna.getGlobalConfig();
    
    public static List<String> blocks = new ArrayList<String>();
    
    public String notify_message, notify_permission;
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e)
    {
        if(e.getBlock() != null)
        {
            if(!DashRays.names.contains(e.getPlayer().getName()))
            {
                if(blocks.contains(e.getBlock().getType().toString()))
                {
                    for(String name : DashRays.names)
                    {
                        if(Bukkit.getPlayerExact(name) != null)
                        {
                            Bukkit.getPlayerExact(name).sendMessage(notify_message.replace("%p%", e.getPlayer().getName()).replace("%b%", e.getBlock().getType().toString().toLowerCase()));
                        };
                    };
                };
            }
        };
    };
    
    boolean default_toggle = config.getBoolean("properties.default-toggle-enabled");
    
    String default_toggle_message = Luna.transStr(config.getString("properties.default-toggle-message"));
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        if(default_toggle)
        {
            if(e.getPlayer().hasPermission(notify_permission))
            {
                Player p = e.getPlayer();
                
                if(!DashRays.names.contains(p.getName()))
                {
                    DashRays.names.add(p.getName());
                    p.sendMessage(default_toggle_message);
                };
            };
        };
    };
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e)
    {
        if(DashRays.names.contains(e.getPlayer().getName()))
            DashRays.names.remove(e.getPlayer().getName());
    };
};
