
// Author: Dashie
// Version: 1.0

package com.dash.security;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor
{
    @Override public boolean onCommand(CommandSender s, Command c, String a, String[] as)
    {
        if (!(s instanceof Player))
            return false;
        
        return true;
    };
};