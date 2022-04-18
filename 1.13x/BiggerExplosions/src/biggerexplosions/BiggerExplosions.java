
// Author: Dashie
// Version: 1.0

package biggerexplosions;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BiggerExplosions extends JavaPlugin implements Listener
{
    @Override public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);
    };
    
    @EventHandler public void onExplosion(EntityExplodeEvent e)
    {
        Entity entity = e.getEntity();
        
        if(entity instanceof Fireball)
        {
            Location location = e.getLocation();
            location.getWorld().createExplosion(location, 3);
            
            return;
        };
    };
};
