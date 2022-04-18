
// Author: Dashie
// Version: 1.0

package com.recoded;

import org.bukkit.ChatColor;

public class Kvinne
{
    public static void print(String line)
    {
        System.out.println("(Dash Wandz): " + line);
    };
    
    public static String color(String line)
    {
        return ChatColor.translateAlternateColorCodes('&', line);
    };    
};
