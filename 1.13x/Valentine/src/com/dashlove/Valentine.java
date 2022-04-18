
// Author: Dashie
// Version: 1.0

package com.dashlove;

import com.dashlove.CommandsHandler;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Valentine extends JavaPlugin
{   
    private static FileConfiguration config = (FileConfiguration) null;
    private static CommandsHandler handler = new CommandsHandler();    
    private static JavaPlugin plugin = (JavaPlugin) null;    
    
    @Override public void onEnable()
    {
        print("The plugin is loading ....");
     
        plugin = (JavaPlugin) this;
        
        LoadConfiguration();
        
        getCommand("dashvalentine").setExecutor(handler);
        getCommand("kiss").setExecutor(handler);
        getCommand("love").setExecutor(handler);        
        getCommand("hug").setExecutor(handler);
        
        print("The plugin has been enabled c:");
    };
    
    public static void LoadConfiguration()
    {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        
        config = (FileConfiguration) plugin.getConfig();
        
        handler.loveyouperm = config.getString("permissions.love");
        handler.adminperm = config.getString("permissions.admin");        
        handler.kissperm = config.getString("permissions.kiss");
        handler.hugperm = config.getString("permissions.hug");
        
        handler.messagereceivformat = color(config.getString("message-receiver-format"));
        handler.messagesendformat = color(config.getString("message-sender-format"));
    };
    
    @Override public void onDisable()
    {
        print("The plugin has been disabled ;c");
    };
    
    public static void print(final String str)
    {
        System.out.println("(Dasies Valentine): " + str);
    };
    
    public static String color(final String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
};