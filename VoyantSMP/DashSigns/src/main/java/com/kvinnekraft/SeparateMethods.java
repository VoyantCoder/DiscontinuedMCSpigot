package com.kvinnekraft;

import org.bukkit.ChatColor;

public interface SeparateMethods
{
    Plugin plugin = new Plugin();

    class Plugin
    {
        final String color(final String data)
        {
            return ChatColor.translateAlternateColorCodes('&', data);
        }

        final void sendLog(final String data)
        {
            System.out.println("(Dash Signs): " + data);
        }
    }
}
