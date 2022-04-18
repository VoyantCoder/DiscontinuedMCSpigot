
// Author: Dashie
// Version: 1.0

package dash.recoded;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterHelp extends JavaPlugin implements CommandExecutor
{
    private FileConfiguration config = (FileConfiguration) null;
    private final JavaPlugin plugin = (JavaPlugin) this;
    
    @Override public void onEnable()
    {
        print("The plugin is loading ....");
        
        LoadConfiguration();
        
        getCommand("help").setExecutor(this);
        
        print("The plugin has been enabled!");
    };
    
    @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
    {
        if (!(s instanceof Player))
        {
            print("\n" + help_message);
            return true;
        };
        
        final Player p = (Player) s;
        
        if (!p.hasPermission(help_permission))
        {
            p.sendMessage(color("&cHey, you are not allowed to use this, huh?"));
            return false;
        };
        
        if (as.length > 0 && p.hasPermission(admin_permission) && as[0].equalsIgnoreCase("reload"))
        {
            p.sendMessage(color("&cReloading plugin configuration ...."));
            
            LoadConfiguration();
            
            p.sendMessage(color("&cPlugin configuration has been reloaded!"));
            return true;
        };
        
        p.sendMessage(help_message);
        return true;
    };
    
    // Perhaps implement a Listener to override external plugins if this causes issues?
    
    private String help_message = "", help_permission, admin_permission;
    
    private void LoadConfiguration()
    {
        saveDefaultConfig();
        
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        if (help_message.length() > 0)
        {
            help_message = "";
        };
        
        for (final String line : config.getStringList("properties.help-message"))
        {
            help_message += color(line) + "\n";
        };
        
        admin_permission = config.getString("properties.admin-permission");
        help_permission = config.getString("properties.help-permission");
    };
    
    @Override public void onDisable()
    {
        print("The plugin has been disabled!");
    };
    
    private String color(String line)
    {
        return ChatColor.translateAlternateColorCodes('&', line);
    };
    
    private void print(String line)
    {
        System.out.println("(Better Help): " + line);
    };
};