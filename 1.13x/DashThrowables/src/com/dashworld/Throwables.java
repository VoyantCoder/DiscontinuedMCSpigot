

// Author: Dashie


package com.dashworld;


import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


public class Throwables extends JavaPlugin implements Listener, CommandExecutor
{
    static FileConfiguration config;
    static JavaPlugin plugin;
    
    
    private void plugin_message()
    {
        print("----------------------");
        print("Author: Dashie");
        print("Version: 1.0");
        print("----------------------");
        print("Email: KvinneKraft@protonmail.com");
        print("Github: https://github.com/KvinneKraft");
        print("----------------------");
    };
    
    
    private void print(String str)
    {
        System.out.println("(Dash Throwables): " + str);
    };
    
    private String color(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
    
    
    @Override public void onEnable()
    {
        print("Enabling ze plugin ....");
        
        saveDefaultConfig();
        
        config = (FileConfiguration) getConfig();
        plugin = (JavaPlugin) this;
        
        plugin_message();
        reload_data();
        
        getServer().getPluginManager().registerEvents(this, plugin);
        getCommand("dashthrowables").setExecutor(plugin);
        
        print("Ze plugin has been enabled!");
    };
    
    
    private String admin_permission;
    
    private final String denied_message = color("&cYou are not supposed to use this, huh?");
    private final String usage_message = color("&cUsage: &7/dashthrowables reload");
    
    private final String reloading_message = color("&aReloading plugin ....");
    private final String reloaded_message = color("&aPlugin has been reloaded!");
    
    
    @Override public boolean onCommand(CommandSender s, Command c, String a, String[] as)
    {
        if(!(s instanceof Player))
        {
            return false;
        };
            
        Player p = (Player) s;
        
        if(!p.hasPermission(admin_permission))
        {
            p.sendMessage(denied_message);
            return false;
        };
        
        if(as.length < 1 || !as[0].equalsIgnoreCase("reload"))
        {
            p.sendMessage(usage_message);
            return false;
        }
        
        else //if(as[0].equalsIgnoreCase("reload"))
        {
            p.sendMessage(reloading_message);
            reload_data();
            p.sendMessage(reloaded_message);
        };
        
        return true;
    };
    
    
    private List<Player> witherskull_cache = new ArrayList<>();    
    private List<Player> fireball_cache = new ArrayList<>();
    private List<World> worlds = new ArrayList<>();
    
    private String fireball_permission, witherskull_permission;
    
    private boolean has_fireballs, fireball_fire, fireball_terrain, has_witherskulls, witherskull_fire, witherskull_terrain;
    private int fireball_cooldown, fireball_radius, witherskull_cooldown, witherskull_radius;
    private double fireball_velocity, witherskull_velocity;
    
    
    private final String witherskull_cooldown_message = color("&cCalm down, you must wait a bit before throwing another one.");    
    private final String fireball_cooldown_message = color("&cYou must wait a bit before throwing another &6Fireball&c!");
    
    private final String witherskull_use_again = color("&aYou may now throw a &8Wither Skull &aagain!");
    private final String fireball_use_again = color("&aYou may now throw a &6Fireball &aagain!");
    
    
    private void reload_data()
    {
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        fireball_terrain = config.getBoolean("dash-throwables.firecharges.explosion.terrain");        
        has_fireballs = config.getBoolean("dash-throwables.firecharges.enabled");
        fireball_fire = config.getBoolean("dash-throwables.firecharges.explosion.fire");
        
        fireball_velocity = config.getDouble("dash-throwables.firecharges.velocity");
        
        fireball_cooldown = config.getInt("dash-throwables.firecharges.cooldown");        
        fireball_radius = config.getInt("dash-throwables.firecharges.explosion.radius"); 
        
        fireball_permission = config.getString("dash-throwables.firecharges.permission");        
        
        witherskull_terrain = config.getBoolean("dash-throwables.witherskulls.explosion.terrain");
        has_witherskulls = config.getBoolean("dash-throwables.witherskulls.enabled");
        witherskull_fire = config.getBoolean("dash-throwables.witherskulls.explosion.fire");
        
        witherskull_velocity = config.getDouble("dash-throwables.witherskulls.velocity");
        
        witherskull_cooldown = config.getInt("dash-throwables.witherskulls.explosion.enabled");
        witherskull_radius = config.getInt("dash-throwables.witherskulls.explosion.radius");
        
        witherskull_permission = config.getString("dash-throwables.witherskulls.permission");
        
        if(worlds.size() > 0)
        {
            worlds.clear();
        };
        
        for(String str : config.getStringList("dash-throwables.allowed-worlds"))
        {
            World world = Bukkit.getWorld(str);
            
            if(world == null)
            {
                print("<" + str + "> is an invalid world. Skipping ....");
                continue;
            };
            
            worlds.add(world);
        };
        
        admin_permission = config.getString("dash-throwables.admin-permission");
    };
    
    @EventHandler public void onEntityProjectileHit(ProjectileHitEvent e)
    {
        if(!(e.getEntity().getShooter() instanceof Player))
        {
            return;
        };
        
        Entity entity = e.getEntity();
        Location location = entity.getLocation();
        
        if(entity.getType().equals(EntityType.FIREBALL))
        {
            location.getWorld().createExplosion(location, fireball_radius, fireball_fire, fireball_terrain);
        }
        
        else if (entity.getType().equals(EntityType.WITHER_SKULL))
        {
            location.getWorld().createExplosion(location, witherskull_radius, witherskull_fire, witherskull_terrain);
        };               
    };
    
    
    @EventHandler public void onEntityExplode(EntityExplodeEvent e)
    {
        Entity entity = e.getEntity();
        
        if(entity instanceof Fireball)
        {
            e.setCancelled(!fireball_terrain);
        }
        
        else if(entity instanceof WitherSkull)
        {
            e.setCancelled(!witherskull_terrain);
        };
    };
    
    
    @EventHandler public void onInteract(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();
        
        if(!worlds.contains(p.getWorld()))
        {
            return;
        };
        
        ItemStack item = e.getItem();
        
        if(item == null)
        {
            return;
        }
            
        if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            if(has_fireballs && item.getType().equals(Material.FIRE_CHARGE) && p.hasPermission(fireball_permission))
            {
               if(fireball_cache.contains(p))
               {
                   p.sendMessage(fireball_cooldown_message);
                   return;
               };
               
               Fireball fireball = p.launchProjectile(Fireball.class);

               fireball.setVelocity(p.getLocation().getDirection().multiply(fireball_velocity));
               fireball.setIsIncendiary(fireball_fire);               
               //fireball.setCustomName("dashball");
               //fireball.setCustomNameVisible(false);
               
               if(p.isOp())
               {
                   return;
               };
               
               fireball_cache.add(p);
               
               getServer().getScheduler().runTaskLater(plugin, 
                    new Runnable() 
                    { 
                        @Override public void run() 
                        { 
                            fireball_cache.remove(p);
                            
                            if(p.isOnline())
                            {
                                p.sendMessage(fireball_use_again);
                            };
                        } 
                    }, 
                    
                    fireball_cooldown * 20
               );
            }
        
            else if(has_witherskulls && item.getType().equals(Material.WITHER_SKELETON_SKULL) && p.hasPermission(witherskull_permission))
            {
                if(witherskull_cache.contains(p))
                {
                    p.sendMessage(witherskull_cooldown_message);
                    return;
                };
                
                WitherSkull witherskull = p.launchProjectile(WitherSkull.class);
                
                witherskull.setVelocity(p.getLocation().getDirection().multiply(witherskull_velocity));
                witherskull.setIsIncendiary(witherskull_fire);
                //witherskull.setCustomName("dashskull");
                //witherskull.setCustomNameVisible(false);
                
                if(p.isOp())
                {
                    return;
                };
                
                witherskull_cache.add(p);
                
                getServer().getScheduler().runTaskLater(plugin, 
                    new Runnable()
                    {
                        @Override public void run()
                        {
                            witherskull_cache.remove(p);                            
                            
                            if(p.isOnline())
                            {
                                p.sendMessage(witherskull_use_again);
                            };
                        };
                    }, 
                    
                    witherskull_cooldown * 20
                );
            };
        };
    };
};
