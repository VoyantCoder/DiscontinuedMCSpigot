
// Author: Dashie
// Version: 1.0

package com.philosophy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Authenticate
{
    final static String[] welcome_message = new String[]
    {
        Freya.color("&7&oI love you all, have fun on this server while"),
        Freya.color("&7&oit is still here, be nice and respectful to"),
        Freya.color("&7&oothers and overall enjoy your time )o(\n")
    };
    
    public static void onPlayerJoin(PlayerJoinEvent e)
    {
        final Player p = (Player) e.getPlayer();
        
        if (!p.hasPlayedBefore())
        {
            e.setJoinMessage(Freya.color("&eWelcome the newbie &a" + p.getName() + " &e!"));
            
            p.sendMessage(Freya.color("&7&oSssh, since it is your first time I will be giving you &e5000$"));
            p.sendMessage(Freya.color("&aYou have received &e5000$"));
            
            Consilience.econ.depositPlayer(p, 5000);
            
            p.teleport(new Location(e.getPlayer().getWorld(), Spawn.x, Spawn.y - 2, Spawn.z));
        }
        
        else
        {
            e.setJoinMessage(Freya.color("&7(&a+&7) &e" + p.getName()));
        };
        
        p.sendMessage(welcome_message);
        
        Bukkit.getServer().getScheduler().runTaskAsynchronously
        (
            Consilience.plugin, 
                
            new Runnable() 
            { 
                @Override public void run()
                {
                    for (final Player _p : Bukkit.getServer().getOnlinePlayers())
                    {
                        _p.playSound(_p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 30, 30);
                    };
                };
            }
        );
    };
    
    public static void onPlayerQuit(PlayerQuitEvent e)
    {
        e.setQuitMessage(Freya.color("&7(&c-&7) &4" + e.getPlayer().getName()));
    };
};