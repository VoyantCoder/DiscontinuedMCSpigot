
// Author: Dashie
// Version: 1.0

package com.dash.security;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class EventListener implements Listener
{
    @EventHandler private void onBlockBreak(BlockBreakEvent e)
    {
        if(e.getPlayer() == null)
            return;        
        
        
    };
    
    @EventHandler private void onBlockPlace(BlockPlaceEvent e)
    {
        if(e.getPlayer() == null)
            return;
        
        
    };
    
    @EventHandler private void onPlayerCommand(PlayerCommandPreprocessEvent e)
    {
        
    };
    
    @EventHandler private void onPlayerChat(AsyncPlayerChatEvent e)
    {
        
    };
    
    @EventHandler private void onPlayerDamage(EntityDamageEvent e)
    {
        if(!(e.getEntity() instanceof Player))
            return;
        
        if(e.getCause() == EntityDamageEvent.DamageCause.FALL)
        {
            
        };
    };
};