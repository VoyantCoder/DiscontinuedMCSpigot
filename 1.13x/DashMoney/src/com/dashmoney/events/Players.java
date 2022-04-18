
// Author: Dashie
// Version: 1.0

package com.dashmoney.events;


import com.dashmoney.Moon;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;


public class Players
{
    FileConfiguration config = Moon.getGlobalConfig();
    
    String message = Moon.transStr(config.getString("properties.players.receive-message"));    
    
    boolean heads = config.getBoolean("properties.players.drop-head");    
    
    Integer max = config.getInt("properties.players.max-drop");
    Integer min = config.getInt("properties.players.min-drop");
      
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
        String name = ((Player)e.getEntity()).getName();
        
        if(heads)
        {
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta playerHeadMeta = (SkullMeta)playerHead.getItemMeta();
            
            playerHeadMeta.setOwningPlayer((Player)e.getEntity());
            playerHead.setItemMeta(playerHeadMeta);
            
            e.getDrops().add(playerHead);
        };
        
        killer.sendMessage(message.replace("%money%", money).replace("%player%", name));
    
        return;
    };
};
