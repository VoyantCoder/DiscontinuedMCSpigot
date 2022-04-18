
// Author: Dashie
// Version: 1.0

package DashSit.src.com.dashcraft;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DashSit extends JavaPlugin
{
    public static JavaPlugin plugin;
    public static FileConfiguration config;
    
    @Override
    public void onEnable()
    {
        Luna.print("Initializing the Sit Plugin ....");
        
        saveDefaultConfig();
        
        config = getConfig();
        plugin = this;
        
        getCommand("sit").setExecutor(new CommandHandler());
        getServer().getPluginManager().registerEvents(new EventsHandler(), plugin);
        
        Luna.print("Author: Dashie");
        Luna.print("Plugin Name: DashSit);");
        Luna.print("Version: 1.0");
        
        Luna.print("The Sit Plugin is now running in the Background!");
    };
    
    @Override
    public void onDisable()
    {
        // Disable Message
    };
};
