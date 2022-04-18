
// Author: Dashie
// Version: 1.0


package com.dashrobot;


import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;


//---//
//---// Dashies Command Handler Class:
//---//

public class Commands implements CommandExecutor
{
    boolean developer_support;
    
    String admin_permission;
    
    String correct_syntax_message = Captcha.color("&7Correct usage: &c/dashcaptcha reload");    
    String not_permitted_message = Captcha.color("&cYou are not supposed to use this, huh?");
    String developer_message = Captcha.color("&cHey, I am Dashie, the Developer of this plugin, see some more of my work at: \n \n&bGithub: &7https://github.com/KvinneKraft/ \n&bWebsite: &7https://pugpawz.com");
    String reloading_message = Captcha.color("&aPlugin is being reloaded ....");
    String reloaded_message = Captcha.color("&aPlugin has been reloaded!");

    
    @Override
    public boolean onCommand(CommandSender s, Command c, String a, String[] as)
    {
        if(!(s instanceof Player))
        {
            return false;
        };
        
        Player p = (Player) s;
        
        if(!p.hasPermission(admin_permission))
        {
            if(developer_support)
            {
                p.sendMessage(developer_message);
            }
            
            else
            {
                p.sendMessage(not_permitted_message);
            };
        }
        
        else if(as.length > 0)
        {
            p.sendMessage(reloading_message);
        
            Refresh.reload_action();
        
            p.sendMessage(reloaded_message);
        }
        
        else
        {
            p.sendMessage(correct_syntax_message);
        };
        
        return true;
    };
};
