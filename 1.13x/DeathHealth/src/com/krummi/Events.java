

// Author: Dashie
// Version: 1.0


package com.krummi;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;


public class Events implements Listener
{
    @EventHandler public void onPlayerDeath(PlayerDeathEvent e)
    {
        if(!(e.getEntity().getKiller() instanceof Player))
            return;
        
        Player k = e.getEntity().getKiller();
        
        final double current_health = k.getHealth();
        double new_health = current_health + 4.5;
        
        if(new_health > 20) new_health = 20;
        
        k.setHealth(new_health);
    };
};