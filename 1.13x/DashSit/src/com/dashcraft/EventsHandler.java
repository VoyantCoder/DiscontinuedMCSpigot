
package DashSit.src.com.dashcraft;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

public class EventsHandler implements Listener
{
    FileConfiguration config = Luna.getGlobalConfig();
    
    String standMessage = Luna.transStr(config.getString("stand-message"));
    
    @EventHandler
    public void onEntityDismount(EntityDismountEvent e)
    {
        if(!(e.getEntity() instanceof Player))
            return;
        
        else if(!(e.getDismounted() instanceof ArmorStand))
            return;
                    
        if(e.getDismounted().getMetadata("chair") != null)
        {
            e.getDismounted().remove();
            
            Player p = (Player) e.getEntity();
            
            p.sendMessage(standMessage);
        };
    };
    
    Luna luna = new Luna();
    
    boolean stairs = config.getBoolean("right-click-stair-sit");
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e)
    {
        if((!stairs) || (e.getClickedBlock() == null))
            return;
        
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if(e.getClickedBlock().getType().toString().toLowerCase().contains("stairs"))
            {
                
                Location location = e.getClickedBlock().getLocation();
                
                location.add(0.5,-1.2,0.5);
                
                luna.SitDown(e.getPlayer(), location);
            };
        };
    };
};
