
// Author: Dashie
// Version: 1.0

package com.dashchat;


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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class DashChatCooldown extends JavaPlugin
{
    FileConfiguration config;
    JavaPlugin plugin = this;
    
    Moony moon = new Moony();
    
    CommandsHandler commands = new CommandsHandler();    
    EventsHandler events = new EventsHandler();
    
    
    @Override
    public void onEnable()
    {
        moon.print("Plugin is being enabled ....");
        
        saveDefaultConfig();
        
        events.doReload();
        
        getServer().getPluginManager().registerEvents(events, plugin);
        getCommand("dashcooldown").setExecutor(commands);
        
        moon.print("Plugin has been enabled!");
    };
    
    
    class CommandsHandler implements CommandExecutor
    {
        boolean f = false, t = true;
        
        String developer_message = moon.transstr("&aCoded by Dashie while she was high. &ehttps://github.com/KvinneKraft");   
        String reloading_message = moon.transstr("&aThe plugin is reloading its data ....");
        String reloaded_message = moon.transstr("&aThe plugin its data has been reloaded!");
        
        String admin_permission;
        
        @Override
        public boolean onCommand(CommandSender s, Command c, String a, String[] as)
        {
            if(!(s instanceof Player))
                return f;
            
            Player p = (Player) s;

            if(!p.hasPermission(admin_permission))
            {
                p.sendMessage(developer_message);
                return f;
            };
            
            p.sendMessage(reloading_message);
            
            events.doReload();
            
            p.sendMessage(reloaded_message);
            
            return t;
        };
    };
    
    
    class EventsHandler implements Listener
    {
        List<String> command_uuid = new ArrayList<>();
        List<String> message_uuid = new ArrayList<>();
        
        String bypass_permission, cooldown_message;
        
        int message_cooldown_delay, command_cooldown_delay;
        boolean message_cooldowns, command_cooldowns;
        
        public void doReload()
        {
            plugin.reloadConfig();
            config = plugin.getConfig();
            
            bypass_permission = config.getString("cooldown-bypass-permission");
            cooldown_message = moon.transstr(config.getString("cooldown-message"));           
            
            if(command_uuid.size() > 0)
                command_uuid.clear();
            
            if(message_uuid.size() > 0)
                message_uuid.clear();
            
            message_cooldown_delay = config.getInt("message-cooldown");
            command_cooldown_delay = config.getInt("command-cooldown");            
            
            message_cooldowns = config.getBoolean("message-cooldowns");
            command_cooldowns = config.getBoolean("command-cooldowns");
            
            commands.admin_permission = config.getString("admin-permission");
        };
        
        @EventHandler
        public void onMessage(AsyncPlayerChatEvent e)
        {
            Player p = e.getPlayer();
        
            if((p.hasPermission(bypass_permission)) || (!message_cooldowns))
                return;
            
            String unique_id = p.getUniqueId().toString();
            
            if(message_uuid.contains(unique_id))
            {
                p.sendMessage(cooldown_message.replace("{second}", String.valueOf(message_cooldown_delay)));
                e.setCancelled(true);
                
                return;
            };       
            
            message_uuid.add(unique_id);
            
            getServer().getScheduler().runTaskLaterAsynchronously(plugin, 
                new Runnable() 
                { 
                    @Override
                    public void run()
                    {
                        if(message_uuid.contains(unique_id))
                        {
                            message_uuid.remove(unique_id);
                        };
                    };
                }, 
                
                message_cooldown_delay * 20
            );
        };
        
        @EventHandler
        public void onCommand(PlayerCommandPreprocessEvent e)
        {
            Player p = e.getPlayer();
        
            if((p.hasPermission(bypass_permission)) || (!command_cooldowns))
                return;
            
            String unique_id = p.getUniqueId().toString();
            
            if(command_uuid.contains(unique_id))
            {
                p.sendMessage(cooldown_message.replace("{second}", String.valueOf(command_cooldown_delay)));
                e.setCancelled(true);
                
                return;
            };
            
            command_uuid.add(unique_id);
            
            getServer().getScheduler().runTaskLaterAsynchronously(plugin, 
                new Runnable() 
                { 
                    @Override 
                    public void run() 
                    { 
                        if(command_uuid.contains(unique_id))
                        {
                            command_uuid.remove(unique_id);
                        };
                    }; 
                },
                
                command_cooldown_delay * 20
            );
        };
        
        @EventHandler
        public void onQuit(PlayerQuitEvent e)
        {
            String unique_id = e.getPlayer().getUniqueId().toString();
            
            if(command_uuid.contains(unique_id))
            {
                command_uuid.remove(unique_id);
            };
            
            if(message_uuid.contains(unique_id))
            {
                message_uuid.remove(unique_id);
            };
        };
    };
    
    
    @Override
    public void onDisable()
    {
        moon.print("Plugin has been disabled!");
    };
    
    
    class Moony
    {
        public void print(String str)
        {
            System.out.println("(Dash Strict Chat Cooldown): " + str);
        };
        
        public String transstr(String str)
        {
            return ChatColor.translateAlternateColorCodes('&', str);
        };
    };        
};
