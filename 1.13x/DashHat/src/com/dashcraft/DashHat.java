

// Author: Dashie
// Version: 1.0


package com.dashcraft;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


public class DashHat extends JavaPlugin implements CommandExecutor
{
    public static void print(String str)
    {
        System.out.println("(Dash Hat): " + str);
    };
    
    public static String color(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
    
    public static FileConfiguration config;
    public static JavaPlugin plugin;
    
    @Override public void onEnable()
    {
        print("Initializing plugin configurations and what not ....");
        
        saveDefaultConfig();
        
        config = (FileConfiguration) getConfig();
        plugin = (JavaPlugin) this;
        
        load();
        
        getCommand("dashhat").setExecutor(plugin);
        
        print("I am done, I am now running!");
    };
    
    @Override public void onDisable()
    {
        print("Plugin has now been disabled.");
    };
    
    private String admin_permission, hat_permission, hat_others_permission;
    
    private void load()
    {
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        admin_permission = config.getString("properties.admin-permission");        
        
        hat_others_permission = config.getString("properties.hat-others-permission");
        hat_permission = config.getString("properties.hat-permission");
    };
    
    @Override public boolean onCommand(CommandSender s, Command c, String a, String[] as)
    {
        if(!(s instanceof Player))
            return false;
        
        Player p = (Player) s;
        
        if (as.length >= 1 && as[0].equalsIgnoreCase("reload") && p.hasPermission(admin_permission))
        {
            p.sendMessage(color("&e>>> &a&lReloading ...."));
            
            load();
            
            p.sendMessage(color("&e>>> &a&lDone!"));
        }
            
        else if ((p.hasPermission(hat_permission) || p.hasPermission(admin_permission)))
        {
            ItemStack item = p.getInventory().getItemInMainHand();
            
            if(item == null || item.getType() == Material.AIR)
            {
                p.sendMessage(color("&cYou must be holding something besides air."));
                return false;
            };
            
            Player t = p;
            
            if(as.length >= 1 && p.hasPermission(hat_others_permission))
            {
                t = Bukkit.getPlayerExact(as[0]);
                
                if(t == null)
                {
                    p.sendMessage(color("&cThe specified player must be online."));
                    return false;
                };
            };
            
            t.getInventory().removeItem(item);            
            
            if(t.getInventory().getHelmet() != null)
            {
                t.getInventory().addItem(p.getInventory().getHelmet());
            };
                        
            t.getInventory().setHelmet(item);
            
            if(t != p)
            {
                p.sendMessage(color("&aYou have given &e" + t.getName() + " &ayour custom head!"));
            };
            
            t.sendMessage(color("&aThere ye go, your custom head!"));
        }
        
        else
        {
            p.sendMessage(color("&cYe do not has the purrmissions do ye?"));
        };
        
        return true;
    };
};
