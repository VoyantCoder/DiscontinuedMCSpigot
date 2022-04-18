
// Author: Dashie
// Version: 1.0

package com.dash;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandsHandler implements CommandExecutor
{
    boolean f = false, t = true;
    
    private void ReloadData()
    {
        FishRewardz.config = FishRewardz.plugin.getConfig();        
        FishRewardz.plugin.reloadConfig();
        
        
    };
    
    String admin_permission = FishRewardz.config.getString("admin-permission");
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String a, String[] as)
    {
        if(!(s instanceof Player))
            return f;
            
        
            
        return t;
    };
};