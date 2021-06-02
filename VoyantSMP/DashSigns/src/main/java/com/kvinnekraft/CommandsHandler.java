package com.kvinnekraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public interface CommandsHandler
{
    class Hook implements CommandExecutor
    {
        @Override
        public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {

            return true;
        }
    }
}
