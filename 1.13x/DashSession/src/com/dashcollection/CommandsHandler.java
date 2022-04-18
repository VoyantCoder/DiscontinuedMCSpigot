
// Author: Dashie
// Version: 1.0

package com.dashcollection;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandsHandler implements CommandExecutor
{
    Session.Moony moon = Session.moon;    
    
    String plug_message = moon.transStr("&eHei, me &dDashie&e, also know as &dPrincess_Freyja &ehas coded this entire plugin.\n\n&eIf you want to get in touch with me or see some more of my work then feel free to visit any of the following links.\n\n&eGithub: &bhttps://github.com/KvinneKraft \n&eTwitter: &bhttps://twitter.com/KraftKvinne \n&eWebsite: &bhttps://pugpawz.com \n&eSpigot MC: &bhttp://bit.ly/KvinneKraftMC");    
    String command_permission;
   
    String correct_use = moon.transStr("&cCorrect use: &7/dashsession reload");
    
    String reloading_message = moon.transStr("&aPlugin is being reloaded ....");
    String reloaded_message = moon.transStr("&aThe plugin has been reloaded!");
    
    boolean f = false, t = true;
    boolean developer_support; 
    
    public void Refresh()
    {
        command_permission = Session.config.getString("optional-properties.command-permission");
        developer_support = Session.config.getBoolean("optional-properties.developer-support");
    };
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String a, String[] as)
    {
        if(!(s instanceof Player))
        {
            return f;
        };
        
        Player p = (Player) s;
        
        if(!p.hasPermission(command_permission))
        {
            if(developer_support) 
            {
                p.sendMessage(plug_message);
            };
                
            return f;
        }
        
        else if(as.length < 1)
        {
            p.sendMessage(correct_use);
            return f;
        };
        
        a = as[0].toLowerCase();
        
        if(a.equals("reload"))
        {
            p.sendMessage(reloading_message);
            
            Session.refreshDashData();
            
            p.sendMessage(reloaded_message);
        }
        
        else 
        {
            p.sendMessage(correct_use);
        };
        
        return t;
    };
};