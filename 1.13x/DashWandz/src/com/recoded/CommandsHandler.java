
// Author: Dashie
// Version: 1.0

package com.recoded;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandsHandler implements CommandExecutor
{
    public static String command_permission;
    
    @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
    {
        if (!(s instanceof Player))
        {
            Kvinne.print("You may only execute this command as a player!");
            return false;
        };
        
        final Player p = (Player) s;
        
        if (!p.hasPermission(command_permission))
        {
            p.sendMessage(Kvinne.color("&cYou lack sufficient permissions!"));
            return false;
        }
        
        else if (as.length >= 1)
        {
            final String ar = as[0].toLowerCase();
            
            if (ar.equals("reload"))
            {
                p.sendMessage(Kvinne.color("&cReloading configuration ...."));
                
                DashWandz.LoadConfiguration();
                
                p.sendMessage(Kvinne.color("&cThe configuration has been reloaded!"));
            }
            
            else if (ar.equals("give") && as.length >= 2)
            {
                final String supposed_wand = as[1].toLowerCase();
                ItemStack item = null;
                
                if (supposed_wand.equals("firework"))
                {
                    item = DashWandz.wands.get(DashWandz.FIREWORK);
                }
                
                else if (supposed_wand.equals("lightning"))
                {
                    item = DashWandz.wands.get(DashWandz.LIGHTNING);
                }
                
                else if (supposed_wand.equals("wither"))
                { 
                    item = DashWandz.wands.get(DashWandz.WITHER);
                }

                else if (supposed_wand.equals("fireball"))
                {
                    item = DashWandz.wands.get(DashWandz.FIREBALL);
                }

                else
                {
                    p.sendMessage(Kvinne.color("&cUse &4/dashwandz list &cfor a valid list of wands."));
                    return false;
                };
                
                final String name = item.getItemMeta().getDisplayName();
                Player _p = p;
                
                if (as.length > 2)
                {
                    _p = Bukkit.getPlayerExact(as[2]);
                    
                    if (_p == null)
                    {
                        p.sendMessage(Kvinne.color("&cThe player specified must be online!"));
                        return false;
                    };
                }
                
                if (_p.equals(p))
                {
                    _p.sendMessage(Kvinne.color("&aYou have given yourself a &r" + name + "&a!"));
                }
                
                else
                {
                    _p.sendMessage(Kvinne.color("&aYou have been given a &r" + name + "&a!"));
                    p.sendMessage(Kvinne.color("&aYou have given &e" + _p.getName() + " &aa &r" + name + "&a!"));                    
                };
                
                _p.getInventory().addItem(item);
            }
            
            else if (ar.equals("list"))
            {
                p.sendMessage(Kvinne.color("&bAvailable Wands: &efirework, lightning, wither and fireball."));
            }
            
            else
            {
                p.sendMessage(help_message);
                return false;
            };
        }
        
        else
        {
            p.sendMessage(help_message);
            return false;
        };
        
        return true;
    };

    private final String help_message = Kvinne.color("&cCorrect syntax: &4&o/dashwands [give | list | reload] <wand> <player>");
};