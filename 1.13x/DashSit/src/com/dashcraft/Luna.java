
package DashSit.src.com.dashcraft;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

public class Luna
{
    public static String transStr(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
    
    public static void print(String str)
    {
        System.out.println(transStr("(Dash Sit): " + str));
    };
    
    public static JavaPlugin getGlobalPlugin()
    {
        return DashSit.plugin;
    };
    
    public static FileConfiguration getGlobalConfig()
    {
        return DashSit.config;
    };
    
    FileConfiguration config = Luna.getGlobalConfig();
    JavaPlugin plugin = Luna.getGlobalPlugin();
    
    String permission = config.getString("permission");
    
    String vehicleMessage = Luna.transStr(config.getString("vehicle-message"));    
    String denyMessage = Luna.transStr(config.getString("denied-message"));
    String sitMessage = Luna.transStr(config.getString("sit-message"));    
    
    public void SitDown(Player p, Location location)
    {
        if(!p.hasPermission(permission))
        {
            p.sendMessage(denyMessage);
            return;
        }
        
        else
        if(p.isInsideVehicle())
        {
            p.sendMessage(vehicleMessage);
            return;
        };
        
        ArmorStand chair = (ArmorStand)location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        chair.setMetadata("chair", new FixedMetadataValue(plugin, "chair"));        
        
        chair.setRotation(0, 0);
        chair.setVisible(false);
        chair.setGravity(false);
        
        chair.addPassenger(p);
        p.sendMessage(sitMessage);
    };
};
