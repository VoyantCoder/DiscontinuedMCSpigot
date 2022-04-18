
// Author: Dashie 
// Version: 1.0

package com.dashrays;

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
        return DashRays.config;
    };       
    
    public static JavaPlugin getGlobalPlugin()
    {
        return DashRays.plugin;
    };
    
    public static void print(String str)
    {
        System.out.println(str);
    };
    
    public static void updateConfig()
    {
        getGlobalConfig().set("properties.blocks", EventsHandler.blocks);
        getGlobalPlugin().saveConfig();
    };
};
