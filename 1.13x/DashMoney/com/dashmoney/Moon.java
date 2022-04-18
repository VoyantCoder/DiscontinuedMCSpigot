
// Author: Dashie
// Version: 1.0

package com.dashmoney;


import java.util.Random;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class Moon
{
    public static String transStr(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
    
    public static FileConfiguration getGlobalConfig()
    {
        return DashMoney.config;
    };
    
    public static JavaPlugin getGlobalPlugin()
    {
        return DashMoney.plugin;
    };
    
    public static Economy getGlobalEconomy()
    {
        return DashMoney.econ;
    };
    
    public static boolean hasVault()
    {
        if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        else
            return true;
    };
    
    public static Integer getRand(int min, int max)
    {
        return new Random().nextInt((max - min) + 1) + min;
    };
    
    public static void print(String str)
    {
        System.out.println(transStr("(Dash Money): " + str));
    };
};
