
// Since this is a plugin for my own minecraft server. (kvinnekraft.serverminer.com) I will be 
// using static (hard coded) values, because it is only for personal use. ^

// Author: Dashie
// Version: 1.0

package com.philosophy;

import org.bukkit.ChatColor;

public class Freya
{
    public static void print(String line)
    {
        System.out.println("(Dash Core): " + line);
    };
    
    public static String color(String line)
    {
        return ChatColor.translateAlternateColorCodes('&', line);
    };
};
