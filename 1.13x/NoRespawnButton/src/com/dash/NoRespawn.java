
package com.dash;

import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NoRespawn extends JavaPlugin
{
    @Override public void onEnable()
    {
        print("I am crawling ....");
        
        print
        (
            (
                "\n-=-=-=-=-=-=-=-=-=-=-=-=-\n" +
                "Author: Dashie (KvinneKraft)\n" +
                "Version: 1.0\n" +
                "Contact: KvinneKraft@protonmail.com\n" +
                "GitHub: https://github.com/KvinneKraft \n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-" 
            )
        );
        
        LoadConfiguration();
        
        getCommand("norespawn").setExecutor(new Commands());
        getServer().getPluginManager().registerEvents(new Events(), plugin);
        
        print("I am alive!");
    };
    
    FileConfiguration config;
    JavaPlugin plugin;
    
    void LoadConfiguration()
    {
        saveDefaultConfig();
        
        if (plugin == null)
            plugin = (JavaPlugin) this;
        
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        if (spawns.size() > 0)
        {
            spawns.clear();
        };
        
        for (final String substance : config.getStringList("spawn-points"))
        {
            String[] substance_genetics = substance.split(" ");
            
            if (substance_genetics.length < 4)
            {
                print("Invalid world configuration has found in the configuration file! Skipping ....");
                continue;
            };
            
            try
            {
                final World world = getServer().getWorld(substance_genetics[0]);
                
                if (world == null)
                {
                    throw new Exception("1");
                };
                
                final Double x = Double.parseDouble(substance_genetics[1]);
                final Double y = Double.parseDouble(substance_genetics[2]);
                final Double z = Double.parseDouble(substance_genetics[3]);
                
                spawns.put(world, new Location(world, x, y, z));
            }
            
            catch (final Exception e)
            {
                print("Invalid world configuration has been found in the configuration file! Skipping ....");
            };
        };
    };
    
    final HashMap<World, Location> spawns = new HashMap<>();
    
    class Events implements Listener
    {
        @EventHandler void onPlayerDamage(final EntityDamageEvent e)
        {
            if (e.getEntity() instanceof Player)
            {
                final Player p = (Player) e.getEntity();
                
                if (spawns.containsKey(p.getWorld()))
                {
                    if (p.getHealth() - e.getDamage() <= 1)
                    {
                        p.setHealth(p.getMaxHealth());

                        for (final PotionEffect substantial : p.getActivePotionEffects())
                        {
                            p.removePotionEffect(substantial.getType());
                        };
                        
                        p.teleport(spawns.get(p.getWorld()));                        
                        p.setFireTicks(0);
                        
                        getServer().getScheduler().runTaskAsynchronously
                        (
                            plugin,
                            
                            new Runnable()
                            {
                                @Override public void run()
                                {
                                    for (final ItemStack substance : p.getInventory().getContents())
                                    {
                                        if (substance != null && substance.getType() != Material.AIR)
                                        {
                                            p.getWorld().dropItemNaturally(p.getLocation(), substance);
                                        };
                                    };
                                };
                            }
                        );
                        
                        p.getInventory().clear();                        
                    };
                };
            };
        };
    };
    
    class Commands implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("I am sorry but you may only do this as a player!");
                return false;
            };
            
            final Player p = (Player) s;
            
            if (p.hasPermission("admin") || p.isOp())
            {
                if (as.length >= 1 && as[0].equalsIgnoreCase("reload"))
                {
                    p.sendMessage(color("&aReloading ...."));
                    
                    LoadConfiguration();
                    
                    p.sendMessage(color("&aDone!"));
                }
                
                else
                {
                    p.sendMessage(color("&cYou should try adding &4&oreload &cas an argument."));
                };
            }
            
            else
            {
                p.sendMessage(color("&cYou may not use this command!"));
            };
            
            return true;
        };
    };
    
    @Override public void onDisable()
    {
        print("I am dead!");
    };
    
    void print(final String line)
    {
        System.out.println("(No Respawn Button): " + line);
    };
    
    String color(final String line)
    {
        return ChatColor.translateAlternateColorCodes('&', line);
    };
};