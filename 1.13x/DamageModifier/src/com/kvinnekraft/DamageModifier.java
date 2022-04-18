
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import java.util.HashMap;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class DamageModifier extends JavaPlugin
{
    @Override public void onEnable()
    {
        print("I am coming alive ....");
        
        LoadConfiguration();
        
        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("damagemodifier").setExecutor(new Commands());
        
        print("\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\n Author: Dashie \nVersion: 1.0 \nContact: KvinneKraft@protonmail.com \nGithub: https://github.com/KvinneKraft \n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        
        print("Okay, I am now alive!");
    };
    
    FileConfiguration config;
    JavaPlugin plugin;
    
    protected void LoadConfiguration()
    {
        saveDefaultConfig();
        
        if (plugin != this)
        {
            plugin = (JavaPlugin) this;
        };
        
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        try
        {
            modifier_use = config.getString("permissions.attack-multiplier-permission");
            admin_permission = config.getString("permissions.plugin-admin-permission");
            
            for (final String data : config.getStringList("damage-modifier.damage-modifier-applier"))
            {
                try
                {
                    final String[] arr = data.split(" ");
                    
                    if (arr.length < 2)
                    {
                        throw new Exception("ERROR");
                    };
                    
                    final Material material = Material.valueOf(arr[0].toUpperCase());
                    
                    if (material == null)
                    {
                        throw new Exception("ERROR");
                    };
                    
                    damage_modifiers.put(material, Double.valueOf(arr[1]));
                }
                
                catch (final Exception e)
                {
                    print("An invalid formatted line of configuration had been found and will be skipped....");
                };
            };
            
            damage_multiplier_chance = config.getInt("damage-modifier.damage-modifier-chance");
        }
        
        catch (final Exception e)
        {
            print("Some configuration error had occurred while reading the configuration file!");
        };
    };
    
    final HashMap<Material, Double> damage_modifiers = new HashMap<>();
    
    String admin_permission, modifier_use;
    
    int damage_multiplier_chance;
    
    protected class Events implements Listener
    {
        @EventHandler public void onPlayerAttack(final EntityDamageByEntityEvent e)
        {
            if (e.getDamager() instanceof Player)
            {
                final Player p = (Player) e.getDamager();
                
                if (p.hasPermission(modifier_use))
                {
                    if (e.getEntity() instanceof LivingEntity)
                    {
                        if (p.getInventory().getItemInMainHand() != null)
                        {
                            final ItemStack object = (ItemStack) p.getInventory().getItemInMainHand();

                            if (object != null)
                            {
                                if (damage_modifiers.containsKey(object.getType()))
                                {
                                    final Random rand = new Random();

                                    if (rand.nextInt(1000) < damage_multiplier_chance)
                                    {
                                        e.setDamage(damage_modifiers.get(object.getType()));
                                    };
                                };
                            };
                        };
                    };
                };
            };
        };
    };
    
    protected class Commands implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only do this as a player!");
                return false;
            };
            
            final Player p = (Player) s;
            
            if (!p.hasPermission(admin_permission))
            {
                p.sendMessage(color("&cYou may not do this!"));
                return false;
            };
            
            if (as.length >= 1)
            {
                if (as[0].equalsIgnoreCase("reload"))
                {
                    p.sendMessage(color("&a> Reloading ...."));
                    
                    LoadConfiguration();
                    
                    p.sendMessage(color("&a> Done!"));
                    
                    return true;
                };
            };
            
            p.sendMessage(color("&cInvalid syntax, valid syntax: &4&o/damagemodifier reload"));
            
            return true;
        };
    };    
    
    @Override public void onDisable()
    {
        print("Oh, I am now dead ;c");
    };
    
    protected void print(final String d)
    {
        System.out.println("(Damage Modifier): " + d);
    };
    
    protected String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };
};