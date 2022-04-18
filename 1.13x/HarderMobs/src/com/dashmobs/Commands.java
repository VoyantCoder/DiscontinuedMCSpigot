

// Author: Dashie
// Version: 1.0


package com.dashmobs;


import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;


public class Commands implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender s, Command c, String a, String[] as)
    {
        if(!(s instanceof Player))
        {
            return false;
        };
        
        return true;
    };
};
