
// Author: Dashie
// Version: 1.0

package dash.recoded;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SocialSpy extends JavaPlugin implements Listener,CommandExecutor
{
    @Override public void onEnable()
    {
        print("Plugin is being loaded ....");
        
        LoadConfiguration();
        
        getServer().getPluginManager().registerEvents(this, plugin);
        getCommand("socialspy").setExecutor(plugin);
        
        print("Plugin has been loaded!");
    };
    
    private FileConfiguration config = (FileConfiguration) null;
    private final JavaPlugin plugin = (JavaPlugin) this;
    
    private void LoadConfiguration()
    {
        saveDefaultConfig();
        
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        command_permission = config.getString("social-spy.reload-permission");        
        bypass_permission = config.getString("social-spy.bypass-permission");
        
        notification_format = color(config.getString("social-spy.notification-format"));
    };
    
    private String bypass_permission, command_permission, notification_format;
    
    @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
    {
        if (!(s instanceof Player))
        {
            print("You must execute this command as a player!");
            return false;
        };
        
        final Player p = (Player) s;
        
        if (!p.hasPermission(command_permission))
        {
            p.sendMessage(color("&cYou possess insufficient permissions!"));
            return false;
        }
        
        else if (as.length >= 1)
        {
            if (as[0].equalsIgnoreCase("reload"))
            {
                p.sendMessage(color("&aReloading configuration ...."));
                
                LoadConfiguration();
                
                p.sendMessage(color("&aSuccessfully reloaded configuration!"));
            }
            
            else if (as[0].equalsIgnoreCase("toggle"))
            {
                ToggleDashSpy(p);
            }
            
            else
            {
                p.sendMessage(color("&cCorrect usage: &4&o/dashspy [toggle | reload]"));
                return false;
            };
        }
        
        else
        {
            ToggleDashSpy(p);
        };
        
        return true;
    };
    
    private final List<Player> players = new ArrayList<>();
    
    private void ToggleDashSpy(final Player p)
    {
        if (players.contains(p))
        {
            p.sendMessage(color("&aYou have toggled Dash Spy &c&lOFF&a!"));
            players.remove(p);
        }
        
        else
        {
            p.sendMessage(color("&aYou have toggled Dash Spy &a&lON&a!"));
            players.add(p);
        };
    };
    
    @EventHandler public void onPlayerCommand(PlayerCommandPreprocessEvent e)
    {
        final Player p = (Player) e.getPlayer();
        
        if (p.hasPermission(bypass_permission) || p.hasPermission(command_permission) || players.contains(p))
        {
            return;
        };
        
        getServer().getScheduler().runTaskAsynchronously
        (
            plugin,
            
            new Runnable()
            {
                @Override public void run()
                {
                    for (final Player _p : players)
                    {
                        _p.sendMessage(notification_format.replace("%p%", p.getName()).replace("%m%", e.getMessage()));
                    };
                };
            }
        );
    };
    
    @Override public void onDisable()
    {
        print("Plugin has been disabled!");
    };
    
    private String color(String line)
    {
        return ChatColor.translateAlternateColorCodes('&', line);
    };
    
    private void print(String line)
    {
        System.out.println("(Better Social Spy): " + line);
    };
};