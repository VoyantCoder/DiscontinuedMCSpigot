

// Author: Dashie
// Version: 1.0


package com.dashmobs;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.EntityType;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.Bukkit;
import java.util.List;


public class DashManager
{   
    private static boolean isMaterial(String str)
    {
        return Material.valueOf(str) != null;
    };
    
    
    private static boolean isEnchant(String str)
    {
        return Enchantment.getByName(str) != null;
    };
    
    
    private static boolean isWorld(String str)
    {
        return Bukkit.getWorld(str) != null;
    };
    
    
    private static boolean isEntity(String str)
    {
        return EntityType.valueOf(str) != null;
    };
    
    
    private static boolean isPotionEffect(String str)
    {
        return PotionEffectType.getByName(str) != null;
    };
    
    
    private static boolean isInteger(String str)
    {
        try
        {
            Integer.valueOf(str);   
            return true;
        }
        
        catch (Exception e)
        {
            return false;
        }
    };
    
    
    public static void reload_plugin()
    {
        HarderMobs.plugin.reloadConfig();
        HarderMobs.config = HarderMobs.plugin.getConfig();
        
        FileConfiguration config = HarderMobs.config;
        
        md.spawn_chance = config.getInt("harder-mobs.mobs-properties.spawning.morphed-chance");               
        md.max_health   = config.getInt("harder-mobs.mobs-properties.spawning.health.max");
        md.min_health   = config.getInt("harder-mobs.mobs-properties.spawning.health.min");  
        
        md.custom_tag = HarderMobs.color(config.getString("harder-mobs.mob-properties.spawning.custom-tag"));
        
        md.allowed_worlds.clear();
        md.mob_types.clear();
        md.effects.clear();
        
        md.max_enchants.clear();        
        md.gears.clear();
        
        md.enchantments.clear();
        md.levels.clear();
        
        for(String str : config.getStringList("harder-mobs.mob-properties.spawning.morphed-mobs"))
        {
            if(!isEntity(str))
            {
                HarderMobs.print("Invalid mob type: " + str + " ! Skipping ....");
                continue;
            };
            
            md.mob_types.add(EntityType.valueOf(str));
        };
        
        for(String str : config.getStringList("harder-mobs.mob-properties.spawning.allowed-worlds"))
        {
            if(!isWorld(str))
            {
                HarderMobs.print("Not found world: " + str + " ! Skipping ....");
                continue;
            };
            
            md.allowed_worlds.add(Bukkit.getWorld(str));
        };
        
        String[] gear_types = new String[] { "helmets", "chestplates", "leggings", "boots", "weapons" };
        
        for(String type : gear_types)
        {
            List<ItemStack> gear_cache = new ArrayList<>();
            
            for(String str : config.getStringList("harder-mobs.mob-properties.gear." + type))
            {
                if(!isMaterial(str))
                {
                    HarderMobs.print("Invalid material type: " + str + " ! Skipping ....");
                    continue;
                };
                
                gear_cache.add(new ItemStack(Material.valueOf(str), 1));
            };
            
            if(gear_cache.size() > 0)
            {
                md.gears.put(type, gear_cache);
            };
            
            for(String str : config.getStringList("harder-mobs.mob-properties.potion-effects"))
            {
                String[] arr = str.split(" ");
                
                if((arr.length != 2) || (!isPotionEffect(arr[0])) || (!isInteger(arr[1])))
                {
                    HarderMobs.print("Invalid potion effect type: " + str + " ! Skipping ....");
                    continue;
                };
                
                md.effects.add(new PotionEffect(PotionEffectType.getByName(arr[0].toUpperCase()), 999999, Integer.valueOf(arr[1])));
            };
            
            List<Enchantment> enchant_cache = new ArrayList<>();
            List<Integer> level_cache = new ArrayList<>();
            
            for(String str : config.getStringList("harder-mobs.mob-properties.enchantments.gear." + type))
            {
                String[] arr = str.split(" ");
                
                if((arr.length != 2) || (!isInteger(arr[1])))
                {
                    HarderMobs.print("Invalid enchantment format found, skipping ....");
                    continue;
                };
                
                String enchant = arr[0];
                
                if(!isEnchant(enchant))
                {
                    HarderMobs.print("Invalid enchantment type: " + enchant + " ! Skipping ....");
                    continue;
                };
                
                enchant_cache.add(Enchantment.getByName(enchant));
                level_cache.add(Integer.valueOf(arr[1]));
            };
            
            if(enchant_cache.size() > 0)
            {
                md.enchantments.put(type, enchant_cache);            
                md.levels.put(type, level_cache);
            };
        };
    };
};
