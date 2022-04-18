
// Author: Dashie
// Version: 1.0

package dash.recoded;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterAutoBroadcast extends JavaPlugin implements CommandExecutor
{
    @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
    {
        if (!(s instanceof Player))
        {
            print("You may only execute this command as a player!");
            return false;
        };
        
        final Player p = (Player) s;
        
        if (!p.hasPermission(admin_permission))
        {
            p.sendMessage(color("&cYou are not permitted to use this, are ye?"));
            return false;
        }
        
        else if (as.length > 0 && as[0].equalsIgnoreCase("reload"))
        {
            p.sendMessage(color("&cReloading configuration ...."));
            
            getServer().getScheduler().cancelTasks(this);
            LoadConfiguration();
            
            p.sendMessage(color("&cDone!"));
        }
        
        else
        {
            p.sendMessage(color("&cSyntax: /dashcaster reload"));
        };
        
        return true;
    };
    
    @Override public void onEnable()
    {
        print("Plugin is being enabled ....");
        
        LoadConfiguration();
        
        getCommand("dashcaster").setExecutor(this);
        
        print("Plugin has been enabled.");
    };
    
    private FileConfiguration config = (FileConfiguration) null;
    private final JavaPlugin plugin = (JavaPlugin) this;
    
    private void LoadConfiguration()
    {
        saveDefaultConfig();
        
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        has_sounds = config.getBoolean("properties.sound-effects.enabled");
        
        if (has_sounds)
        {
            try
            {
                sound = (Sound) Sound.valueOf("properties.sound-effects.sound");
            }
            
            catch (final Exception e)
            {
                print("Invalid sound type in the config.yml!");
                has_sounds = false;
            };
        };
        
        if (messages.size() > 0)
        {
            messages.clear();
        };
        
        for (final String line : config.getStringList("properties.messages"))
        {
            messages.add(color(line));
        };
            
        if (messages.size() < 1)
        {
            print("No messages were set in the config.yml!");
        };
        
        interval = (int) config.getInt("properties.announce-interval") * 20;
        admin_permission = (String) config.getString("properties.admin-permission");
        
        getServer().getScheduler().runTaskTimer
        (
            plugin, 
                
            new Runnable() 
            { 
                final Server server = (Server) getServer();
                final Random rand = new Random();
                
                @Override public void run() 
                {  
                    server.broadcastMessage(messages.get(rand.nextInt(messages.size())));
                    
                    server.getScheduler().runTaskAsynchronously
                    (
                        plugin, 
                            
                        new Runnable() 
                        { 
                            @Override public void run() 
                            {  
                                for (final Player p : server.getOnlinePlayers())
                                {
                                    server.getScheduler().runTask
                                    (
                                        plugin, 
                                            
                                        new Runnable() 
                                        { 
                                            @Override public void run() 
                                            {
                                                p.playSound(p.getLocation(), sound, 30, 30);
                                            }; 
                                        }
                                    );
                                };
                            }; 
                        }
                    );
                }; 
            }, 
            
            interval, 
            interval
        );        
    };
    
    private boolean has_sounds;
    private int interval;        
    
    private String admin_permission;    
    private Sound sound;
    
    private List<String> messages = new ArrayList<>();
    
    @Override public void onDisable()
    {
        print("Plugin has been disabled!");
    };
    
    private void print(String line)
    {
        System.out.println("(Better RTP): " + line);
    };
    
    private String color(String line)
    {
        return ChatColor.translateAlternateColorCodes('&', line);
    };
};