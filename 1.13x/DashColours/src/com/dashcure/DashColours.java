
package com.dashcure;


// Author: Dashie
// Version: 1.0


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.configuration.file.FileConfiguration;


public class DashColours extends JavaPlugin
{
    public FileConfiguration config = getConfig();
    public JavaPlugin plugin = this;
    
    List<String> player_toggle_cache; 
    List<String> player_db;    
    
    CommandsHandler commands_handler = new CommandsHandler();
    EventsHandler events_handler = new EventsHandler();        
    
    @Override
    public void onEnable()
    {
        Muun.print("Loading Dash Colors 1.0 ....");
        
        saveDefaultConfig();
        refreshData();  
        
        getServer().getPluginManager().registerEvents(events_handler, plugin);
        getCommand("dashcolor").setExecutor(commands_handler);
        
        Muun.print("Dash Colors 1.0 has been loaded!");
    };
    
    
    class EventsHandler implements Listener
    {
        @EventHandler
        public void onPlayerChat(AsyncPlayerChatEvent e)
        {
            String player_id = e.getPlayer().getUniqueId().toString();
            
            if((!commands_handler.user_exists(player_id)) || (!player_toggle_cache.contains(player_id)))
                return ;
            
            String message = commands_handler.get_color(player_id) + e.getMessage();
            
            e.setMessage(Muun.transStr(message));
        };
    };
    
    
    public void refreshData()
    {
        plugin.reloadConfig();
        plugin.getConfig();
        
        config = plugin.getConfig();
        
        commands_handler.admin_permission = config.getString("admin-command-permission");
        commands_handler.command_permission = config.getString("command-permission");
        commands_handler.command_magic_permission = config.getString("command-magic-permission");
         
        commands_handler.toggle_off_message = Muun.transStr(config.getString("toggle-off-message"));        
        commands_handler.toggle_on_message = Muun.transStr(config.getString("toggle-on-message"));
        
        commands_handler.missing_key_message = Muun.transStr(config.getString("missing-key-message"));
        commands_handler.correct_syntax = Muun.transStr(config.getString("invalid-syntax-message"));        

        commands_handler.add_message = Muun.transStr(config.getString("player-add-message"));
        commands_handler.del_message = Muun.transStr(config.getString("player-del-message"));
        
        commands_handler.player_offline_message = Muun.transStr(config.getString("player-offline-message"));
        commands_handler.color_set_message = Muun.transStr(config.getString("color-set-message"));
        
        commands_handler.invalid_format_message = Muun.transStr(config.getString("invalid-format-message"));
        
        commands_handler.exist_message = Muun.transStr(config.getString("exists-message"));
        commands_handler.not_exist_message = Muun.transStr(config.getString("not-exist-message"));
        
        LoadColorData();
    };
    
    private void LoadColorData()
    {
        if(player_db == null)
        {
            player_toggle_cache = new ArrayList<>();                
            player_db = new ArrayList<>();
        };               
        
        Object toggle_cache_obj = config.get("toggle-table");
        player_toggle_cache = (List)toggle_cache_obj;     
        
        Object player_db_obj = config.get("color-table");
        player_db = (List)player_db_obj;
    };
    
    private void SaveColorData()
    { 
        config.set("toggle-table", player_toggle_cache);        
        config.set("color-table", player_db);
        
        plugin.saveConfig();
        config = plugin.getConfig();
    };    
    
    
    class CommandsHandler implements CommandExecutor
    {
        boolean t = true, f = false;
        
        String exist_message, not_exist_message, color_set_message, insufficient_permission_message, player_offline_message, invalid_format_message, add_message, del_message, toggle_on_message, toggle_off_message, admin_permission, command_permission, missing_key_message, command_magic_permission, correct_syntax;
        
        String reloading_message = Muun.transStr("&aReloading &d&lD&5&la&d&ls&5&lh&d&lC&5&lo&d&ll&5&lo&d&lr&5&ls &a....");
        String reloaded_message = Muun.transStr("&aSuccessfully reloaded &d&lD&5&la&d&ls&5&lh&d&lC&5&lo&d&ll&5&lo&d&lr&5&ls &a!");
        
        String plugin_information_message = Muun.transStr("&eHei, this plugin has been made by Princess_Freyja aka Dashie.\nMore of her work at: &b&lhttps://github.com/KvinneKraft");
        
        private boolean user_exists(String uuid)
        {
            for(String key : player_db)
            {
                if(key.length() > 0)
                {
                    if(key.split(":")[0].equals(uuid))
                    {
                        return t;
                    };
                };
            };
            
            return f;
        };
        
        private String get_color(String uuid)
        {
            for(String key : player_db)
            {
                if(key.length() > 0)
                {
                    if(key.split(":")[0].equals(uuid))
                    {
                        return key.split(":")[1];
                    };
                };
            };
            
            return null;
        };
        
        public void remove_user(String uuid)
        {            
            for(int i = 0; i < player_db.size(); i += 1)
            {
                String kr = player_db.get(i);                
                
                if(kr.length() > 0)
                {      
                    if(kr.split(":")[0].equals(uuid))
                    {
                        player_db.remove(new String(kr));
                    };
                };
            };
        };
        
        @Override
        public boolean onCommand(CommandSender s, Command c, String a, String[] as)
        {
            if(!(s instanceof Player))
                return f;
            
            Player p = (Player) s;
            
            if((as.length > 0) && (as[0].toLowerCase().equals("info")))
            {
                p.sendMessage(plugin_information_message);
                return f;
            }
                
            else if(!p.hasPermission(command_permission))
            {
                p.sendMessage(insufficient_permission_message);
                return f;
            }
            
            else if(as.length < 1)
            {
                p.sendMessage(correct_syntax);
                return f;
            };
            
            a = as[0];
            
            if(!p.hasPermission(admin_permission))
            {
                p.sendMessage(insufficient_permission_message);
            }
                
            else if((a.equals("set")) && (as.length >= 2))
            {
                String color_code = as[1].toLowerCase();
                
                if(Muun.transStr(color_code + "a").equals(color_code + "a"))
                {
                    p.sendMessage(invalid_format_message);
                    return f;
                };
                    
                String unique_id = p.getUniqueId().toString();
                String key = unique_id + ":" + color_code;
                
                if(user_exists(unique_id))
                    remove_user(unique_id);
                
                player_db.add(key);                
                
                if(!player_toggle_cache.contains(unique_id))
                    player_toggle_cache.add(unique_id);
                
                p.sendMessage(color_set_message);
                
                SaveColorData();
            }
            
            else if(a.equals("toggle"))
            {
                String unique_id = p.getUniqueId().toString();
                
                if((player_db == null) || (!user_exists(unique_id)))
                {
                    p.sendMessage(missing_key_message);
                    return f;
                }
                    
                else if(player_toggle_cache.contains(unique_id))
                {
                    player_toggle_cache.remove(unique_id);
                    p.sendMessage(toggle_off_message);
                }
                
                else
                {
                    player_toggle_cache.add(unique_id);
                    p.sendMessage(toggle_on_message);
                };
                
                SaveColorData();
            }            
            
            else if(a.equals("reload"))
            {
                p.sendMessage(reloading_message);
                
                refreshData();
                
                p.sendMessage(reloaded_message);
            }
            
            else if(as.length >= 2)
            {
                if((a.equals("add")) || (a.equals("del")))
                {
                    Player sp = Bukkit.getPlayerExact(as[1]);                    
                    
                    if(sp == null)
                    {
                        p.sendMessage(player_offline_message);
                        return f;
                    };

                    String unique_id = sp.getUniqueId().toString();                    
                    
                    if((a.equals("add")) || (as.length >= 3))
                    {                    
                        if(user_exists(unique_id))
                        {
                            p.sendMessage(exist_message);
                            return f;
                        };                            
                        
                        String color_code = as[2].toLowerCase();                        
                        
                        if(Muun.transStr(color_code + "a").equals(color_code + "a"))
                        {
                            p.sendMessage(invalid_format_message);
                            return f;
                        }          
                        
                        if(!player_toggle_cache.contains(unique_id))
                            player_toggle_cache.add(unique_id);
                        
                        String key = unique_id + ":" + color_code;
                        p.sendMessage(add_message);
                    
                        player_db.add(key);                        
                    }
                
                    else
                    {
                        if(!user_exists(unique_id))
                        {
                            p.sendMessage(not_exist_message);
                            return f;
                        };                         
                        
                        player_toggle_cache.remove(unique_id);
                        remove_user(unique_id);
                    
                        p.sendMessage(del_message);
                    };
                    
                    SaveColorData();
                }
                
                else
                {
                    p.sendMessage(correct_syntax);
                };
            }
            
            else 
            {
                p.sendMessage(correct_syntax);
            };
            
            return t;
        };
    };
    
    
    @Override
    public void onDisable()
    {
        Muun.print("Saving Data ....");
        
        SaveColorData();
        
        Muun.print("Dash Colors 1.0 has been disabled!");
    };
};


class Muun
{
    public static void print(String str)
    {
        System.out.println("(Dash Colours): " + str);
    };
    
    public static String transStr(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
};
