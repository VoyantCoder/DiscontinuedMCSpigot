
// Author: Dashie
// Version: 1.0

package com.recoded;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class DashWandz extends JavaPlugin
{
    public static final List<ItemStack> wands = Arrays.asList
    (
        new ItemStack[]
        {
            new ItemStack(Material.NETHER_WART, 1),
            new ItemStack(Material.NETHER_STAR, 1),
            new ItemStack(Material.STICK, 1),
            new ItemStack(Material.BLAZE_ROD, 1)
        }
    );
    
    public static final int FIREWORK = 0, LIGHTNING = 1, WITHER = 2, FIREBALL = 3;
    
    @Override public void onEnable()
    {
        Kvinne.print("Plugin is being enabled ....");
        
        saveDefaultConfig();
        
        plugin = (JavaPlugin) this;
        
        LoadConfiguration();
        
        getServer().getPluginManager().registerEvents(new EventsHandler(), plugin);
        getCommand("wandz").setExecutor(new CommandsHandler());
        
        Kvinne.print("Plugin has been enabled.");
    };
    
    public static FileConfiguration config = (FileConfiguration) null;
    public static JavaPlugin plugin = (JavaPlugin) null;
    
    public static void LoadConfiguration()
    {
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        CommandsHandler.command_permission = config.getString("dash-wands.command-permission");
        
        if (!wands.get(FIREWORK).hasItemMeta())
        {
            final String[] names = new String[]
            {
                "&e&lF&6i&er&6e&ew&6o&er&6k &e&lW&6a&en&6d", 
                "&f&l&nL&fightning &f&l&nW&fand", 
                "&8&lW&8and &0&lO' &8&lW&8ithering", 
                "&6&lF&6ireball &6&lW&6and"
            };
            
            final String[] lores = new String[]
            {
                "&6&oThrow fireworks at your enemies!", 
                "&f&oThrow down lightning at those who bother you!", 
                "&8&oWatch them all wither away!", 
                "&e&oSee your enemies run from you!"
            };
            
            for (int id = 0; id < wands.size(); id += 1)
            {
                ItemMeta meta = (ItemMeta) wands.get(id).getItemMeta();
                meta.setDisplayName(Kvinne.color(names[id]));
                
                meta.setLore
                (
                    Arrays.asList
                    (
                        new String[] 
                        { 
                            Kvinne.color(lores[id]) 
                        }
                    )
                );
                
                wands.get(id).setItemMeta(meta);
            };
        };
        
        if (EventsHandler.perms.size() > 0)
        {
            EventsHandler.perms.clear();
        };
        
        if (EventsHandler.cooldowns.size() > 0)
        {
            EventsHandler.cooldowns.clear();
        };
        
        final String[] nodes = new String[]
        {
            "firework-wand", "lightning-wand", "wither-wand", "fireball-wand"
        };
        
        for (final String node : nodes)
        {
            try
            {
                final int cooldown = config.getInt("dash-wands." + node + ".cooldown"); 
                EventsHandler.cooldowns.add(cooldown);
                
                final String permission = config.getString("dash-wands." + node + ".use-permission");
                EventsHandler.perms.add(permission);
            }
            
            catch (final Exception e)
            {
                continue;
            }
        };
        
        EventsHandler.bypass_permission = config.getString("dash-wands.bypass-permission");
    };
    
    @Override public void onDisable()
    {
        Kvinne.print("Plugin has been disabled!");
    };
};