
// Author: Dashie
// Version: 1.0


package com.sociallinks;


import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class Dash extends JavaPlugin implements Listener
{   
    public FileConfiguration config = getConfig();
    public JavaPlugin plugin = this;
    
    
    @Override
    public void onEnable()
    {
        moon.Print("The plugin is now being enabled ....");
        
        saveDefaultConfig();
        eUtil.reload();
        
        getServer().getPluginManager().registerEvents(this, plugin);
        
        moon.Print("The plugin has been enabled.");
    };
    
    @Override
    public void onDisable()
    {
        moon.Print("Plugin has been disabled.");
    };

    
    Moony moon = new Moony();
    
    
    class Moony
    {
        public void Print(String str)
        {
            System.out.println("(Social Links): " + str);
        };
        
        public String TransStr(String str)
        {
            return ChatColor.translateAlternateColorCodes('&', str);
        };
    };
    
    
    EventUtil eUtil = new EventUtil();
    
    
    class EventUtil
    {
        public void reload()
        {
            plugin.reloadConfig();
            config = plugin.getConfig();
            
            reloading_message = moon.TransStr(config.getString("reloading-message"));
            reloaded_message = moon.TransStr(config.getString("reloaded-message"));
    
            permission_denied_message = moon.TransStr(config.getString("permission-denied-message"));
            invalid_arguments_message = moon.TransStr(config.getString("invalid-arguments-message"));
            admin_permission = config.getString("admin-permission");       
            
            website_command_permission = config.getString("commands.website.permission");    
            website_command = config.getString("commands.website.command");
    
            website_message = moon.TransStr(config.getString("commands.website.message"));     
        };
    };
    
    
    String website_command, website_command_permission, website_message;
     
    List<String> social_links_commands = Arrays.asList(
        new String[]
        {
            "sociallinks", "sociallink", "sl", "dashlinks"
        }
    );
    
    String invalid_arguments_message, reloading_message, reloaded_message, permission_denied_message, admin_permission;
    
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e)
    {
        String[] cmd = e.getMessage().toLowerCase().split(" ");
        Player p = e.getPlayer();
        
        if(cmd[0].equals(website_command))
        {
            if(!p.hasPermission(website_command_permission))
            {
                p.sendMessage(permission_denied_message);
            }
            
            else 
            {
                p.sendMessage(website_message);
            };
            
            e.setCancelled(true);
            return ;
        }
        
        else if(cmd.length >= 2)
        {
            if(social_links_commands.contains(cmd[0]))
            {
                if(!p.hasPermission(admin_permission))
                {
                    p.sendMessage(permission_denied_message);                       
                }
                
                else if(cmd[1].equals("reload"))
                {
                    p.sendMessage(reloading_message);
                    
                    eUtil.reload();
                    
                    p.sendMessage(reloaded_message);
                }
                
                else
                {
                    p.sendMessage(invalid_arguments_message);
                };
                
                e.setCancelled(true);
                return ;
            };
        };
    };
};
