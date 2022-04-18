
// Author: Dashie
// Version: 1.0

package com.dashmoney.events;


import com.dashmoney.Moon;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;


public class HostileMobs
{
    FileConfiguration config = Moon.getGlobalConfig();
    
    String message = Moon.transStr(config.getString("properties.hostile-mobs.receive-message"));    
    
    Integer max = config.getInt("properties.hostile-mobs.max-drop");
    Integer min = config.getInt("properties.hostile-mobs.min-drop");
    
    Economy econ = Moon.getGlobalEconomy();
    
    public void onDeath(EntityDeathEvent e)
    {
        Entity entity = e.getEntity().getKiller();
    
        if(!(entity instanceof Player))
            return;
        
        Player killer = e.getEntity().getKiller();
       
        int reward = Moon.getRand(min, max);
        
        econ.depositPlayer(killer, reward);
        
        String money = String.valueOf(reward);
        String name = e.getEntity().getName().toLowerCase();
        
        killer.sendMessage(message.replace("%money%", money).replace("%entity%", name));
    
        return;
    };
};
