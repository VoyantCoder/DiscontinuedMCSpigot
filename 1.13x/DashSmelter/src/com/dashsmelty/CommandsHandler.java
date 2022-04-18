
// Author: Dashie
// Version: 1.0

package com.dashsmelty;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandsHandler implements CommandExecutor
{   
    List<String> msg = Arrays.asList(
        new String[]
        {
            Moon.transStr("&cProper Syntax: &7/dashsmelt [reload | add | del] <source> <result> <result amount>"),
            Moon.transStr("&aReloading the config ...."),
            Moon.transStr("&aThe config has been reloaded."),
            Moon.transStr("&aThe recipe has been added."),
            Moon.transStr("&cThe recipe already exists."),
            Moon.transStr("&aThe recipe has been removed."),
            Moon.transStr("&cThe recipe does not exist."),
            Moon.transStr("&cYou are not supposed to use this command, huh?"),
            Moon.transStr("&cThe material given is not valid."),
        }
    );
    
    String command_permission = Moon.getGlobalConfig().getString("command-permission");
    
    @Override
    public boolean onCommand(CommandSender s, Command command, String arg, String[] args)
    {
        if(!(s instanceof Player))
            return false;
        
        Player p = (Player)s;
        
        if(!p.hasPermission(command_permission))
        {
            p.sendMessage(msg.get(7));
            return false;
        }
        
        else if(args.length < 1)
        {
            p.sendMessage(msg.get(0));
            return false;
        };
        
        arg = args[0].toLowerCase();
        
        if(arg.equals("reload"))
        {
            p.sendMessage(msg.get(1));
        
            DashSmelter.furnace.RegisterRecipes();
            
            p.sendMessage(msg.get(2));
        }
        
        // Check if source and result are items.
        // Check if amount is an integer.
        // Make it not require _
        else if((arg.equals("add")) && (args.length >= 4))
        {
            String source = args[1].toUpperCase();
            
            if(!DashSmelter.furnace.doesSourceExist(source))
            {
                String result = args[2].toUpperCase();
                String amount = args[3];
                
                if((Material.getMaterial(source) == null) || (Material.getMaterial(result) == null) || (Integer.valueOf(amount) == null))
                {
                    p.sendMessage(msg.get(8));
                    return false;
                };
                    
                String recipe = source + " " + result + " " + amount;
                
                DashSmelter.furnace.raw_melt_recipes.add(recipe);
                DashSmelter.furnace.SetConfigRecipes();
                
                p.sendMessage(msg.get(3));
            }
            
            else
            {
                p.sendMessage(msg.get(4));
            };
        }
        
        // Check if source and result are items.
        // Check if amount is an integer.
        // Make it not require _
        else if(arg.equals("del") && (args.length >= 2))
        {
            String source = args[1];
            
            if(DashSmelter.furnace.doesSourceExist(source))
            {
                int index = DashSmelter.furnace.indexOfSource(source);
                
                if(index == -1)
                {
                    p.sendMessage(msg.get(6));
                };
                
                DashSmelter.furnace.raw_melt_recipes.remove(index);
                DashSmelter.furnace.SetConfigRecipes();
                
                p.sendMessage(msg.get(5));
            }
            
            else
            {
                p.sendMessage(msg.get(6));
            };
        }
        
        else
        {
            p.sendMessage(msg.get(0));
        };
        
        return true;
    };
};
