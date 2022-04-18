// Author: Dashie
// Version: 1.0

package com.recoded;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DashSecurity extends JavaPlugin
{
    public static FileConfiguration config = (FileConfiguration) null;
    public static JavaPlugin plugin = (JavaPlugin) null;
    
    public static com.recoded.captcha.Captcha captcha;
    public static com.recoded.iplock.IPLock iplock;
    public static com.recoded.login.Login login;
    
    private final DashSec sec = new DashSec();
    
    public static void LoadPlugin()
    {
        captcha = new com.recoded.captcha.Captcha();
        iplock = new com.recoded.iplock.IPLock();
        login = new com.recoded.login.Login();
        
        captcha.Enable();
        iplock.Enable();
        login.Enable();
        
        
    };
    
    @Override public void onEnable()
    {
        sec.print("Loading the plugin ....");
        
        saveDefaultConfig();
        
        config = (FileConfiguration) getConfig();
        plugin = (JavaPlugin) this;
        
        LoadPlugin();        
        
        getServer().getPluginManager().registerEvents(new GlobalEvents(), plugin);
        
        sec.print("The plugin has been loaded!");
    };
    
    @Override public void onDisable()
    {
        getServer().getScheduler().cancelTasks(plugin);
     
        sec.print("The plugin has been disabled!");
    };
};