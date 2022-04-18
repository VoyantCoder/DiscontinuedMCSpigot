
// Author: Dashie
// Version: 1.0

package com.dash;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class FishRewardz extends JavaPlugin
{
    public static FileConfiguration config;
    public static JavaPlugin plugin;
    
    public static CommandsHandler commands;    
    public static EventsHandler events;
    
    @Override
    public void onEnable()
    {
        Moony.Print("The plugin is initializing ....");
        
        saveDefaultConfig();
        
        config = getConfig();
        plugin = this;
        
        commands =  new CommandsHandler();
        events = new EventsHandler();
        
        if((commands != null) && (events != null))
        {
            if(events.reward_materials.size() < 1)
            {
                List<String> rewards = config.getStringList("fishy-rewards");
                
                for(String reward : rewards)
                {
                    String[] arr = reward.split(" ");
                    
                    if(arr.length < 3)
                    {
                        Moony.Print("Invalid reward found: " + reward + ".");
                        Moony.Print("The size of the array received is too small.");
                        
                        continue;
                    };
                    
                    Material material = Material.getMaterial(arr[0]);
                    Integer amount = Integer.valueOf(arr[1]);
                    Double chance = Double.valueOf(arr[2]);
                    
                    if((material == null) || (amount < 1) || (chance < 0))
                    {
                        Moony.Print("Invalid reward found: " + reward + ".");
                        Moony.Print("The format is invalid.");
                        
                        continue;
                    };
                    
                    events.reward_materials.add(new ItemStack(material, amount));
                    events.reward_chances.add(chance);
                };
            };
            
            getServer().getPluginManager().registerEvents(events, this);
            
            // For some reason it says that the setExecutor(); could be possibly receiving
            // a null-pointer, so for that sake am I checking for a null-pointer, dumb ass
            // IDE ^
           
            getCommand("fishyrewards").setExecutor(commands);
        };
        
        Moony.Print("The plugin is now enabled!");
    };
    
    @Override
    public void onDisable()
    {
        Moony.Print("The plugin is now disabled.");
    };
};

class Moony
{
    public static String transStr(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
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
        System.out.println("(Dash Fishyyy): " + str);
    };
};    