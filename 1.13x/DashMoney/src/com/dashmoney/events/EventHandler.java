
// Author: Dashie
// Version: 1.0

package com.dashmoney.events;


import com.dashmoney.Moon;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;


public class EventHandler implements Listener
{
    FileConfiguration config = Moon.getGlobalConfig();
    
    boolean friendlysEnabled = config.getBoolean("properties.friendly-mobs.enabled");
    boolean hostilesEnabled = config.getBoolean("properties.hostile-mobs.enabled");    
    boolean playersEnabled = config.getBoolean("properties.players.enabled");
    
    FriendlyMobs friendly_mobs = new FriendlyMobs();
    HostileMobs hostile_mobs = new HostileMobs();
    Players players = new Players();
    
    @org.bukkit.event.EventHandler
    public void onEntityDeath(EntityDeathEvent e)
    {   
        LivingEntity entity = e.getEntity();
        
        if(friendlysEnabled)
            if(entity instanceof Creature)
                if(!(((Creature)entity) instanceof Monster))
                    friendly_mobs.onDeath(e);
        
        if(hostilesEnabled)
            if(entity instanceof Creature)
                if(((Creature)entity) instanceof Monster)
                    hostile_mobs.onDeath(e);
        
        if(playersEnabled)
            if(entity instanceof Player)
                players.onDeath(e);
    };
    
    String playerDeathMessage = Moon.transStr(config.getString("properties.players.death-message"));
    
    @org.bukkit.event.EventHandler
    public void onPlayerDeath(PlayerDeathEvent e)
    {
        if((!playersEnabled) || (playerDeathMessage.length() < 1))
            return;
        
        if(!(e.getEntity().getKiller() instanceof Player))
            return;
        
        Player killer = e.getEntity().getKiller();
        Player victim = e.getEntity().getPlayer();
        
        ItemStack item = killer.getInventory().getItemInMainHand();
        String item_name = item.getType().toString().replace("_", " ").toLowerCase();
        
        if(item.hasItemMeta())
            if(item.getItemMeta().hasDisplayName())
                item_name = item.getItemMeta().getDisplayName();
        
        e.setDeathMessage(playerDeathMessage.replace("%item%", item_name).replace("%killer%", killer.getName()).replace("%victim%", victim.getName()));
    };
};
