
// Author: Dashie
// Version: 1.0

package com.dashtrolling;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class FakeOP extends JavaPlugin implements CommandExecutor
{
    private FileConfiguration config = (FileConfiguration) null;
    private final JavaPlugin plugin = (JavaPlugin) this;
    
    @Override public void onEnable()
    {
        print("The plugin is loading ....");
        
        LoadConfiguration();
        
        getCommand("fakeop").setExecutor(this);
        
        print("The plugin has been enabled!");
    };
    
    private void LoadConfiguration()
    {
        saveDefaultConfig();
        
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        private_dm = config.getBoolean("private-message");
        message = color(config.getString("op-message"));
        permission = config.getString("permission");
    };
    
    private boolean private_dm;
    private String permission, message;
    
    @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
    {
        if (!(s instanceof Player))
        {
            print("You may only execute this command as a player.");
            return false;
        };
        
        final Player p = (Player) s;
        
        if (!p.hasPermission(permission))
        {
            p.sendMessage(color("&cYou have insufficient permissions."));
            return false;
        }
        
        else if (as.length < 1)
        {
            p.sendMessage(color("&cUsage: /fakeop <player name>"));
            return false;
        }
        
        else if (as[0].equalsIgnoreCase("reload"))
        {
            p.sendMessage(color("&a&lReloading ...."));
            LoadConfiguration();
            p.sendMessage(color("&a&lDone!"));
        }
        
        else 
        {
            final Player t = (Player) getServer().getPlayerExact(as[0]);
            
            if (t == null || t.equals(p))
            {
                p.sendMessage(color("&cYou may only specify online players that are not you."));
                return false;
            };
            
            p.sendMessage(color("&a&lSuccess!"));
            
            if (private_dm)
            {
                t.sendMessage(message.replace("%p%", t.getName()));
            }
            
            else
            {
                getServer().broadcastMessage(message);
            };
        };
        
        return true;
    };
    
    @Override public void onDisable()
    {
        print("The plugin has been disabled!");
    };
    
    private String color(String line)
    {
        return ChatColor.translateAlternateColorCodes('&', line);
    };    
    
    private void print(String line)
    {
        System.out.println("(Fake OP): " + line);
    };
};
