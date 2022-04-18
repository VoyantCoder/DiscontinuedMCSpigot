
/*-=- THIS PROJECT HAS BEEN DISCONTINUED -=-*/

// Author: Dashie
// Version: 1.0

package dash.recoded;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BCC extends JavaPlugin implements Listener, CommandExecutor
{
    private FileConfiguration config = (FileConfiguration) null;
    private final JavaPlugin plugin = (JavaPlugin) this;
    
    @Override public void onEnable()
    {
        print("The plugin is loading ....");
        
        LoadConfiguration();
        
        getServer().getPluginManager().registerEvents(this, plugin);
        getCommand("betterchatcooldowns").setExecutor(this);

        print("This plugin has been discontinued, the near future shall bring a better Chat Cooldown plugin! I promise.  If you have any suggestions then please reach out to me at KvinneKraft@protonmail.com.  Thank you for using this and much love to all of you )o(");
        print("The plugin has been enabled!");
    };
    
    private String cooldown_message, bypass_permission, admin_permission, kick_message;
    private int cooldown, max_spam;
    
    private void LoadConfiguration()
    {
        saveDefaultConfig();
        
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        cooldown_message = color(config.getString("chat-cooldowns.cooldown-message"));
        kick_message = color(config.getString("chat-cooldowns.kick-message"));
        
        bypass_permission = config.getString("chat-cooldowns.bypass-permission");
        admin_permission = config.getString("chat-cooldowns.admin-permission");
        
        cooldown = config.getInt("chat-cooldowns.cooldown") * 20;
        max_spam = config.getInt("chat-cooldowns.kick-when-tried");
    };
    
    @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
    {
        if (!(s instanceof Player))
        {
            print("You can only use this command as a player!");
            return false;
        };
        
        final Player p = (Player) s;
        
        if (!p.hasPermission(admin_permission))
        {
            p.sendMessage(color("&cYou have insufficient permissions!"));
            return false;
        }
        
        else if (as.length >= 1 && as[0].equalsIgnoreCase("reload"))
        {
            p.sendMessage(color("&cReloading configuration ...."));
            
            LoadConfiguration();
            
            p.sendMessage(color("&cConfiguration has been reloaded!"));
        }
        
        else
        {
            p.sendMessage(color("&cCorrect usage: &4&o/bcc reload"));
            return false;
        };
        
        return true;
    };

    private final HashMap<Player, Integer> waitress = new HashMap<>();
    private final HashMap<Player, Integer> players = new HashMap<>();
    
    @EventHandler public void onPlayerMessage(AsyncPlayerChatEvent e)
    {
        final Player p = (Player) e.getPlayer();
        
        if (players.containsKey(p))
        {
            if (players.get(p) > max_spam)
            {
                getServer().getScheduler().runTask
                (
                    plugin, 
                        
                    new Runnable() 
                    { 
                        @Override public void run() 
                        {
                            p.kickPlayer(kick_message);

                            players.remove(p);
                            waitress.remove(p);
                        }; 
                    }
                );
                
                e.setCancelled(true);
                
                return;
            };

            p.sendMessage(cooldown_message.replace("%seconds%", waitress.get(p) + ""));
            players.put(p, players.get(p) + 1);
            e.setCancelled(true);

            return;
        }
        
        else if (p.hasPermission(bypass_permission) || p.hasPermission(admin_permission))
        {
            return;
        };

        waitress.put(p, cooldown / 20);
        players.put(p, 1);
        
        getServer().getScheduler().runTaskLater
        (
            plugin, 
                
            new Runnable() 
            { 
                @Override public void run() 
                { 
                    if (players.containsKey(p))
                    {
                        players.remove(p);
                    };
                }; 
            }, 
            
            cooldown
        );

        new BukkitRunnable()
        {
            @Override public void run()
            {
                if (waitress.containsKey(p))
                {
                    final int val = waitress.get(p);

                    waitress.put(p, val - (val > 0 ? 1 : 0));

                    if (waitress.get(p) <= 0)
                    {
                        waitress.remove(p);
                    };
                };
            }
        }.runTaskTimerAsynchronously(plugin, 20, cooldown * 20 + 10);
        
        return;
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
        System.out.println("(Better Chat Cooldowns): " + line);
    };
};