
// Author: Dashie
// Version: 1.0

package com.dash.security;

import org.bukkit.ChatColor;

public class DashUtility
{
    public static void print(String str)
    {
        System.out.println("(Dash Security): " + str);
    };
    
    public static String color(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
};
