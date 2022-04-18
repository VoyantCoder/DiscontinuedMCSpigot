
// Author: Dashie
// Version: 1.0

package dash.recoded;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Restrictor extends JavaPlugin
{
    FileConfiguration config;
    JavaPlugin plugin;
    
    void LoadConfiguration()
    {
        saveDefaultConfig();
        
        if (plugin != this)
        {
            plugin = (JavaPlugin) this;
        };
        
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        
    };
    
    @Override public void onEnable()
    {
        print("I am reincarnating ....");
        
        LoadConfiguration();
        
        print
        (
            (
                "\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\n" +
                " Author: Dashie, KvinneKraft, Freya\n" + 
                " Version: 1.0                           \n" + 
                " Contact: KvinneKraft@protonmail.com    \n" +
                " Github: https://github.com/KvinneKraft \n" +                        
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-" 
            )
        );
        
        print("I am now alive!");
    };
    
    protected class Events implements Listener
    {
        /*
        - Cooldowns
        - Prohibited = Prevent
        - Modules First
        */
    };
    
    @Override public void onDisable()
    {
        print("I am now dead.");
    };
    
    void print(final String d)
    {
        System.out.println("(Dash Strict): " + d);
    };
    
    String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };
};