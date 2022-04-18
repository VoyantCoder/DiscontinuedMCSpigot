
// Author: Dashie
// Version: 1.0

package com.dashlove;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandsHandler implements CommandExecutor
{
    public String adminperm,hugperm,kissperm,loveyouperm,messagereceivformat, messagesendformat;
    
    @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
    {
        if(!(s instanceof Player))
        {
            Valentine.print("This command is only for in-game players!");
            return false;
        };
        
        final Player p = (Player) s;
        final String command = c.toString().toLowerCase().replace("org.bukkit.command.plugincommand(", "").replace(", dashiesvalentine v1.0)", "");
        
        if(command.equals("dashvalentine"))
        {
            if(!p.hasPermission(adminperm))
            {
                insufficient_permissions(p);
                return false;
            }
            
            else if(as.length >= 1 && as[0].equalsIgnoreCase("reload"))
            {
                p.sendMessage(Valentine.color("&e>>> &aReloading configuration ...."));
                
                Valentine.LoadConfiguration();
                
                p.sendMessage(Valentine.color("&e>>> &aDone!"));
            }
            
            else
            {
                p.sendMessage(Valentine.color("&cTry using &c&lreload &cas an option ;)"));
                return false;
            };
        }
        
        else if((command.equals("hug") || command.equals("kiss") || command.equals("loveyou")) && as.length >= 1)
        {
            if(!p.hasPermission(hugperm) && command.equals("hug")) { insufficient_permissions(p); return false; }
            else if(!p.hasPermission(kissperm) && command.equals("kiss")) { insufficient_permissions(p); return false; }
            else if(!p.hasPermission(loveyouperm) && command.equals("loveyou")) { insufficient_permissions(p); return false; };         
            
            final Player r = (Player) Bukkit.getServer().getPlayerExact(as[0]);
            
            if(r == null)
            {
                p.sendMessage(Valentine.color("&cThe player specified must be online!"));
                return false;
            }
            
            else if(r == p)
            {
                p.sendMessage(Valentine.color("&cAnd how are you going to do that?"));
                return false;
            };
            
            r.sendMessage(messagereceivformat.replace("%w%", command).replace("%p%", p.getName()).replace("loveyou", "ball of love"));
            p.sendMessage(messagesendformat.replace("%w%", command).replace("%p%", r.getName()).replace("loveyou", "ball of love"));
        }
        
        else
        {
            p.sendMessage(Valentine.color("&cCorrect syntax: &7&o/" + command + " <player>"));
            return false;
        };
        
        return true;
    };
    
    private void insufficient_permissions(Player p)
    {
        p.sendMessage(Valentine.color("&cYou lack sufficient permissions ;c"));
    };
};