
// Author: Dashie
// Version: 1.0

package crystal;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Crystal extends JavaPlugin
{
    private FileConfiguration config = (FileConfiguration) null;
    private final JavaPlugin plugin = (JavaPlugin) this;
    
    @Override public void onEnable()
    {
        print("Plugin is loading ....");
        
        LoadConfig();
        
        print("Plugin has been loaded!");
    };
    
    private void LoadConfig()
    {
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        saveDefaultConfig();
    };
    
    @Override public void onDisable()
    {
        print("Plugin has been disabled!");
    };
    
    private void print(String str)
    {
        System.out.println("(Crystal): " + str);
    };
    
    private String color(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
};