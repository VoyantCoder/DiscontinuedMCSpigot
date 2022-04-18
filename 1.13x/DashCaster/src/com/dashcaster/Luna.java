
// Author: Dashie 
// Version: 1.0

package com.dashrays;

import com.dashcaster.AutoBroadcaster;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Luna
{
    public static String transStr(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    }; 
    
    public static FileConfiguration getGlobalConfig()
    {
        return AutoBroadcaster.config;
    };       
    
    public static JavaPlugin getGlobalPlugin()
    {
        return AutoBroadcaster.plugin;
    };
    
    public static void print(String str)
    {
        System.out.println(transStr("(Dashcaster): " + str));
    };
};
