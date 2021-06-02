package com.kvinnekraft;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public interface Configurator
{
    Config config = new Config();

    class Config
    {
        public void LoadConfiguration(DashSigns pluginInst, FileConfiguration configInst)
        {
            pluginInst.saveDefaultConfig();
            pluginInst.reloadConfig();


        }
    }
}
