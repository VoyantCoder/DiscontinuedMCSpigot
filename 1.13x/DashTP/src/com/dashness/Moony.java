
// Author: Dashie
// Version: 1.0

package com.dashness;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class Moony
{
    public static String transStr(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
    
    public static FileConfiguration getGlobalConfig()
    {
        return DashTP.config;
    };
    
    public void DetonateFirework(Location location, Color mcolor, Color fcolor, FireworkEffect.Type type)
    {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta firework_meta = firework.getFireworkMeta();
        
        firework_meta.addEffect(FireworkEffect.builder().withColor(mcolor).with(type).flicker(true).withFlicker().withTrail().withFade(fcolor).trail(true).build());
        
        firework.setFireworkMeta(firework_meta);
        firework.detonate();
    };    
    
    public static void Print(String str)
    {
        System.out.println(transStr("(DashTP): " + str));
    };
    
    public static void SaveConfig()
    {
        DashTP.plugin.saveConfig();
    };
};
