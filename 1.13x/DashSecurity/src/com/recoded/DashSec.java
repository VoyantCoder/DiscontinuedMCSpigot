package com.recoded;

import org.bukkit.ChatColor;

public class DashSec
{
    public void print(final String line)
    {
        System.out.println("(Dash Security): " + line);
    };
    
    public String color(final String line)
    {
        return ChatColor.translateAlternateColorCodes('&', line);
    };
};