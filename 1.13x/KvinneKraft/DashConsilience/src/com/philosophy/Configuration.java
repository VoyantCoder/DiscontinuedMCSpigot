
// Since this is a plugin for my own minecraft server. (kvinnekraft.serverminer.com) I will be 
// using static (hard coded) values, because it is only for personal use. ^

// Author: Dashie
// Version: 1.0

package com.philosophy;

import org.bukkit.configuration.file.FileConfiguration;

public class Configuration 
{
    public static void LoadConfiguration()
    {   
        Consilience.plugin.reloadConfig();
        Consilience.config = (FileConfiguration) Consilience.plugin.getConfig();
        
        
    };
};
