
// Author: Dashie 
// Version: 1.0

package com.dashrays;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

public class CommandsHandler implements CommandExecutor
{
    FileConfiguration config = Luna.getGlobalConfig();
    
    String toggle_on_message, toggle_off_message, added_message, deleted_message, block_not_listed_message, 
           block_is_listed_message, block_nofou_message, invalid_syntax, notify_permission, permission_deny_message,
           admin_permission, reloading_message, reload_finish_message;
    
    boolean f = false, t = true;
    
    public void refreshData()
    {
        DashRays.plugin.getConfig();
        DashRays.plugin.reloadConfig();
            
        DashRays.config = DashRays.plugin.getConfig();
        config = DashRays.config;        
        
        toggle_on_message = Luna.transStr(config.getString("properties.toggle-on-message"));
        toggle_off_message = Luna.transStr(config.getString("properties.toggle-off-message"));
    
        added_message = Luna.transStr(config.getString("properties.add-block-message"));
        deleted_message = Luna.transStr(config.getString("properties.del-block-message"));
    
        block_not_listed_message = Luna.transStr(config.getString("properties.block-not-in-list-message"));
        block_is_listed_message = Luna.transStr(config.getString("properties.block-found-message"));    
        block_nofou_message = Luna.transStr(config.getString("properties.block-not-found-message"));
    
        invalid_syntax = Luna.transStr(config.getString("properties.invalid-syntax-message"));
        notify_permission = config.getString("properties.notify-permission");
    
        permission_deny_message = Luna.transStr(config.getString("properties.permission-denied-message"));
        admin_permission = config.getString("properties.admin-permission");
    
        reloading_message = Luna.transStr(config.getString("properties.reloading-message"));
        reload_finish_message = Luna.transStr(config.getString("properties.reload-finish-message"));
        
        DashRays.events_handler.notify_message = Luna.transStr(config.getString("properties.notify-message"));
        DashRays.events_handler.notify_permission = config.getString("properties.notify-permission");        
        DashRays.events_handler.blocks = config.getStringList("properties.blocks");
    };
    
    @Override
    public boolean onCommand(CommandSender s, Command cmd, String arg, String[] args)
    {
        if(!(s instanceof Player))
            return false;
        
        Player p = (Player) s;
        
        if(!p.hasPermission(admin_permission))
        {
            p.sendMessage(permission_deny_message);
            return f;
        }
        
        else if(args.length < 1)
        {
            p.sendMessage(invalid_syntax);
            return f;
        };
        
        arg = args[0];    
        
        if(arg.equals("toggle"))
        {
            if(DashRays.names.contains(p.getName()))
            {
                p.sendMessage(toggle_off_message);
                DashRays.names.remove(p.getName());
            }
            
            else
            {
                p.sendMessage(toggle_on_message);
                DashRays.names.add(p.getName());
            };
        }
        
        else if(arg.equals("reload"))
        {
            p.sendMessage(reloading_message);
            
            refreshData();
            
            p.sendMessage(reload_finish_message);
        }        
        
        else if(args.length < 2)
        {
            p.sendMessage(invalid_syntax);
            return f;
        }
        
        else if((arg.equals("add")) || (arg.equals("del")))
        {
            String block = "NONE";
            
            for(int id = 1; id < args.length; id += 1)
            {
                if(block.contains("NONE"))
                    block = block.replace("NONE", "");
                
                block += args[id].toUpperCase() + "_";
            };
            
            block = block.substring(0, block.length() - 1);
            Material material = Material.getMaterial(block);
            
            if(material == null)
            {
                p.sendMessage(block_nofou_message);
                return f;
            }
            
            // I am optimizing this in the next update:
            if(arg.equals("add"))
            {
                if(EventsHandler.blocks.contains(block))
                {
                    p.sendMessage(block_is_listed_message);
                    return f;
                };

                EventsHandler.blocks.add(block);
                Luna.updateConfig();
                
                p.sendMessage(added_message.replace("%b%", block));
            }
            
            else if(arg.equals("del"))
            {
                if(!EventsHandler.blocks.contains(block))
                {
                    p.sendMessage(block_not_listed_message.replace("%b%", block));
                    return f;
                };
                    
                EventsHandler.blocks.remove(block);
                Luna.updateConfig();
                
                p.sendMessage(deleted_message.replace("%b%", block));
            }
        }
        
        else
        {
            p.sendMessage(invalid_syntax);
            return f;
        };
            
        return t;
    };
};
