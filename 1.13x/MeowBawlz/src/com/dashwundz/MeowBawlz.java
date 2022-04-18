

// Author: Dashie
// Version: 1.0


package com.dashwundz;


import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


public class MeowBawlz extends JavaPlugin
{
    private String color(String str) { return ChatColor.translateAlternateColorCodes('&', str); };
    private void print(String str) { System.out.println("(Meow Bawlz): " + str); };
    
    private final Commands command = new Commands();
    private final Events events = new Events();
    
    private FileConfiguration config;
    private JavaPlugin plugin;
    
    @Override public void onEnable()
    {
        print("The plugin is loading ....");
        
        saveDefaultConfig();
        
        config = (FileConfiguration) getConfig();
        plugin = (JavaPlugin) this; 
        
        reload();
        
        getServer().getPluginManager().registerEvents(events, plugin);
        getCommand("meowballs").setExecutor(command);
        
        print("The plugin is now done loading and has now been enabled!");
    };
    
    private void reload()
    {
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        bypass_cooldown = config.getBoolean("properties.admin-bypass-cooldown");
        
        prohibited_message = color(config.getString("properties.messages.prohibited-message"));
        shoot_message = color(config.getString("properties.messages.shoot-message"));
        await_message = color(config.getString("properties.messages.await-message"));
        again_message = color(config.getString("properties.messages.again-message"));
        
        if(shoot_message.length() < 1 || await_message.length() < 1 || again_message.length() < 1)
        {
            print("You have not set a message for shoot-message, again-message or await-message! Check config.yml!");
        };
        
        shoot_permission = config.getString("properties.shoot-permission");
        admin_permission = config.getString("properties.admin-permission");
        
        if(shoot_permission.length() < 1 || admin_permission.length() < 1)
        {
            print("You have not set a permission for either shoot-permission or and admin-permission! Check config.yml!");
        };
        
        shoot_cooldown = config.getInt("properties.shoot-cooldown") * 20;
    
        wand_name = color(config.getString("properties.wand-meta.wand-name"));        
        
        Material wi = Material.getMaterial(config.getString("properties.wand-meta.wand-item"));
        
        if(wi == null)
        {
            print("Ehm, the item " + wi.toString() + " is invalid, using BLAZE_ROD instead! Check config.yml!");
            wi = Material.BLAZE_ROD;
        };
        
        wand_item = new ItemStack(wi, 1);
        
        final ItemMeta meta = wand_item.getItemMeta();

        meta.setDisplayName(wand_name);    

        if(wand_lore.size() > 0)
        {
            wand_lore.clear();
        };
        
        for(String str : config.getStringList("properties.wand-meta.wand-lore"))
        {
            wand_lore.add(color(str));
        };
        
        meta.setLore(wand_lore);

        wand_item.setItemMeta(meta);     
        
        meow_permission = config.getString("properties.meowiez.meow-permission");
        meow_message = color(config.getString("properties.meowiez.meower-message"));
        meow_cooldown = config.getInt("properties.meowiez.meow-cooldown") * 20;
    };
    
    private String shoot_permission, admin_permission, shoot_message, await_message, again_message, prohibited_message;
    
    private boolean bypass_cooldown;
    private int shoot_cooldown;    
    
    private final Particle[] particles = new Particle[] 
    {
        Particle.HEART, Particle.SMOKE_LARGE, 
        Particle.FLAME, Particle.CLOUD, 
        Particle.DRAGON_BREATH, Particle.VILLAGER_HAPPY,
        Particle.VILLAGER_ANGRY
    };    
    
    class Events implements Listener 
    {
        final List<Player> players = new ArrayList<>();
        
        @EventHandler public void ProjectileHit(ProjectileHitEvent e)
        {
            if(!(e.getEntity() instanceof Snowball))
            {
                return;
            };
            
            final Snowball snowball = (Snowball) e.getEntity();
            
            if(snowball.getCustomName() == null || !snowball.getCustomName().equals("meowball") || !(e.getEntity().getShooter() instanceof Player))
            {
                return;
            };
            
            try
            {
                final Location location = snowball.getLocation();
                final World world = location.getWorld();
                
                for(Particle particle : particles)
                {
                    world.spawnParticle(particle, location, 16, 1, 1, 1);
                };          
                               
                world.playSound(location, Sound.ENTITY_CAT_AMBIENT, 30, 30);
                world.playSound(location, Sound.ENTITY_CAT_PURREOW, 30, 30);                
                world.playSound(location, Sound.ENTITY_CAT_PURR, 30, 30);
            } 
            
            catch (Exception d)
            {
                // Nothing;
            }
        };
        
        @EventHandler public void ItemUse(PlayerInteractEvent e)
        {
            final ItemStack item = e.getItem();
            
            if(item == null || !item.equals(wand_item))
            {  
                return ;
            };
            
            final Player p = e.getPlayer();
            
            if(!p.hasPermission(shoot_permission) && !p.hasPermission(admin_permission))
            {
                p.sendMessage(prohibited_message);
                return ;
            }
            
            else if(players.contains(p))
            {
                p.sendMessage(await_message);
                return ;
            }
            
            else if(!p.hasPermission(admin_permission) || (p.hasPermission(admin_permission) && !bypass_cooldown))
            {
                players.add(p);
            };            
            
            final Snowball snowball = p.launchProjectile(Snowball.class);
            snowball.setCustomName("meowball");
            
            if(players.contains(p))
            {
                Bukkit.getServer().getScheduler().runTaskLater(plugin, 
                    new Runnable() 
                    { 
                        @Override public void run() 
                        {
                            if(players.contains(p))
                            {
                                players.remove(p);
                            
                                if(p.isOnline())
                                {
                                    p.sendMessage(again_message);
                                };
                            };
                        }; 
                    }, 
                
                    shoot_cooldown
                );          
            };
        };
    };
    
    private final List<Player> meowy_players = new ArrayList<>();
    private final List<String> wand_lore = new ArrayList<>();
    
    private String wand_name, meow_permission, meow_message;    
    private ItemStack wand_item;   
    
    private int meow_cooldown;
    
    class Commands implements CommandExecutor
    {
        private final String permission_denied = color("&cYou are not allowed to use this command, are ye?");
        private final String invalid_command = color("&cCorrect usage: &7/meowbawlz [reload | give] <player>");
        private final String is_offline = color("&cThe supposed receiver should be online!");
        private final String gave_other = color("&aYour gift was received!");
        private final String gave_self = color("&aYou have given yourself a wand!");
        private final String reloading = color("&e>>> &aReloading the plugin ....");
        private final String reloaded = color("&e>>> &aThe plugin has been reloaded!");
        
        @Override public boolean onCommand(CommandSender s, Command c, String a, String[] as)
        {
            if(!(s instanceof Player))
            {
                final Player r = (Player) getServer().getPlayerExact(as[1]);
                
                if (r == null)
                {
                    return false;
                };
                
                r.sendMessage(color("&aYou have received a " + wand_name + " &acongratulations!"));
                r.getInventory().addItem(wand_item);
                
                return true;
            };
            
            final Player p = (Player) s;
            
            if(as.length >= 1 && as[0].equalsIgnoreCase("meow") && p.hasPermission(meow_permission))
            {
                if(meowy_players.contains(p))
                {
                    p.sendMessage(color("&dYe gotta wait before typing that again frend!"));
                    return false;
                };
                
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_CAT_AMBIENT, 30, 30);
                
                meowy_players.add(p);
                
                Bukkit.getScheduler().runTaskLater(plugin, 
                    new Runnable() 
                    {
                        @Override public void run()
                        {
                            if(meowy_players.contains(p))
                            {
                                meowy_players.remove(p);
                            };
                            
                            if(p.isOnline())
                            {
                                p.sendMessage(color("&dYe may now meow again!"));
                            };
                        };
                    }, 
                    
                    meow_cooldown
                );
                
                return true;
            }
            
            else if(!p.hasPermission(admin_permission))
            {
                p.sendMessage(permission_denied);
            }
            
            else if(as.length < 1)
            {
                p.sendMessage(invalid_command);
            }
            
            else if(as[0].equalsIgnoreCase("reload"))
            {
                p.sendMessage(reloading);
                
                reload();
                
                p.sendMessage(reloaded);
            }
            
            else if(as[0].equalsIgnoreCase("give"))
            {
                Player r = p;
                
                if(as.length >= 2)
                {
                    r = Bukkit.getPlayerExact(as[1]);
                    
                    if(r == null || !r.isOnline()) 
                    {
                        p.sendMessage(is_offline);
                        return false;
                    };
                };
                
                r.getInventory().addItem(wand_item);
                
                if(p == r)
                {
                    p.sendMessage(gave_self);
                }
                    
                else
                {
                    p.sendMessage(gave_other);
                };                
            }
            
            else
            {
                p.sendMessage(invalid_command);
            };
            
            return true;
        };
    };
    
    @Override public void onDisable()
    {
        print("The plugin has been disabled!");
    };
};
