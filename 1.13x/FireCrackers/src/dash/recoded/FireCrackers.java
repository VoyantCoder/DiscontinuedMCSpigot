
// Author: Dashie
// Version: 1.0

package dash.recoded;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class FireCrackers extends JavaPlugin
{
    FileConfiguration config;
    JavaPlugin plugin;
    
    protected void LoadConfiguration()
    {
        if (plugin != this)
        {
            plugin = (JavaPlugin) this;
        };
        
        saveDefaultConfig();
        
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        firecracker_cooldown = config.getInt("standard-firecracker.firecracker-cooldown");
        explosion_radius = config.getInt("standard-firecracker.explosion-radius");
        explosion_timer = config.getInt("standard-firecracker.explosion-timer");
        
        explosion_fireworks = config.getBoolean("standard-firecracker.explosion-fireworks");
        
        admin_permission = config.getString("permissions.plugin-admin");
        use_permission = config.getString("permissions.plugin-use");
        
        final ItemMeta fire_meta = (ItemMeta) normal_firecracker.getItemMeta();
        
        fire_meta.setDisplayName(color("&d"));
        fire_meta.setLore
        (
            Arrays.asList
            (
                new String[] 
                {
                    "&6&oThe firework goes boom!"
                }
            )
        );
        
        normal_firecracker.setItemMeta(fire_meta);
    };
    
    String admin_permission, use_permission;    
    int explosion_radius, explosion_timer, firecracker_cooldown;
    boolean explosion_fireworks;
    
    @Override public void onEnable()
    {
        print("I am loading up ....");
        
        LoadConfiguration();
        
        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("firecracker").setExecutor(new Commands());
        
        print("\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\nAuthor: Dashie\nVersion: 1.0\nContact: KvinneKraft@protonmail.com\nGithub: https://github.com/KvinneKraft \n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        print("I am now up and running!");
    };
    
    protected class Events implements Listener
    {
        final List<Player> queue = new ArrayList<>();
        
        @EventHandler public void onPlayerInteract(final PlayerInteractEvent e)
        {
            final Player p = (Player) e.getPlayer();
            
            if (e.getItem() != null && p.hasPermission(use_permission))
            {
                if (e.getItem().isSimilar(normal_firecracker))
                {
                    if (queue.contains(p))
                    {
                        p.sendMessage(color("&cYou must wait before using another one of those!"));
                        e.setCancelled(true);
                        return;
                    };

                    final Snowball firecracker = p.launchProjectile(Snowball.class);

                    firecracker.setCustomNameVisible(false);                
                    firecracker.setMetadata(color("&kdata"), new FixedMetadataValue(plugin, color("&kdata")));

                    if (p.isOp())
                    {
                        return;
                    };

                    e.getItem().setAmount(e.getItem().getAmount() - 1);                

                    queue.add(p);

                    getServer().getScheduler().runTaskLater
                    (
                        plugin,

                        new Runnable()
                        {
                            @Override public void run()
                            {
                                if (p.isOnline())
                                {
                                    p.sendMessage(color("&aYou may now throw another one!"));
                                };

                                if (queue.contains(p))
                                {
                                    queue.remove(p);
                                };
                            };
                        },

                        firecracker_cooldown * 20
                    );
                };
            };
        };
        
        public void DetonateFirework(Location location, Color mcolor, Color fcolor, FireworkEffect.Type type)
        {
            Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            FireworkMeta firework_meta = firework.getFireworkMeta();

            firework_meta.addEffect(FireworkEffect.builder().withColor(mcolor).with(type).flicker(true).withFlicker().withTrail().withFade(fcolor).trail(true).build());

            firework.setFireworkMeta(firework_meta);
            firework.detonate();
        };          
        
        @EventHandler public void onProjectileHit(final ProjectileHitEvent e)
        {
            if (e.getEntity() instanceof Snowball)
            {
                final Snowball firecracker = (Snowball) e.getEntity();
                final Location location = (Location) firecracker.getLocation();
                
                if (firecracker.hasMetadata(color("&kdata")))
                {
                    BukkitTask worker = getServer().getScheduler().runTaskTimerAsynchronously
                    (
                        plugin, 
                            
                        new Runnable() 
                        { 
                            @Override public void run() 
                            { //playEffect(location, effect, id, data, offsetX, offsetY, offsetZ, speed, particleCount, radius);
                                location.getWorld().spawnParticle(Particle.SMOKE_NORMAL, location, 1, 0, 1, 0, 0);
                            }; 
                        },
                        
                        0,
                        2
                    );
                    
                    e.getEntity().remove();
                    
                    getServer().getScheduler().runTaskLater
                    (
                        plugin, 
                            
                        new Runnable() 
                        { 
                            @Override public void run() 
                            { 
                                final Random rand = new Random();
                                
                                location.getWorld().createExplosion(location, explosion_radius, false, false);                 
                                worker.cancel();
                                
                                if (explosion_fireworks)
                                {
                                    final Color rgb = Color.fromRGB(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
                                    DetonateFirework(location, rgb, rgb, FireworkEffect.Type.BURST);
                                };
                            };
                        },
                        
                        explosion_timer * 20
                    );
                };
            };
        };
    };
    
    final ItemStack normal_firecracker = new ItemStack(Material.SNOWBALL, 1);
    
    protected class Commands implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only use this as a Player!");
                return false;
            };
            
            final Player p = (Player) s;
            
            if (p.hasPermission(admin_permission))
            {
                if (as.length >= 1)
                {
                    if (as[0].equalsIgnoreCase("reload"))
                    {
                        p.sendMessage(color("&aDoing work ....")); 

                        LoadConfiguration();

                        p.sendMessage(color("&aDone!!!"));
                    }

                    else if (as[0].equalsIgnoreCase("give"))
                    {
                        Player r = (Player) p;
                        int g = 1;

                        if (as.length >= 2)
                        {
                            r = getServer().getPlayerExact(as[1]);

                            if (r == null)
                            {
                                p.sendMessage(color("&cThe player specified must be online!"));
                                return false;
                            };

                            if (as.length >= 3)
                            {
                                try
                                {
                                    g = Integer.getInteger(as[2]);
                                    if (g < 1) g = 1;
                                }

                                catch (final Exception e)
                                {
                                    p.sendMessage(color("&cThe amount specified must be numeric!"));
                                    return false;
                                };
                            };
                        };

                        normal_firecracker.setAmount(g);
                        r.getInventory().addItem(normal_firecracker);

                        if (r.equals(p))
                        {
                            p.sendMessage(color("&aYou have given yourself a &eFire Cracker&a!"));
                        }

                        else
                        {
                            r.sendMessage(color("&aYou have received a fancy &eFire Cracker&a!"));
                            p.sendMessage(color("&aYou have given &e" + r.getName() + " &aa &eFire Cracker&a!"));
                        };
                    }

                    else
                    {
                        p.sendMessage(color("&cIncorrect syntax, correct syntax: &4&o/fireworks [reload | give] <player> <amount>"));
                    };
                }
                
                else
                {
                    p.sendMessage(color("&cIncorrect syntax, correct syntax: &4&o/fireworks [reload | give] <player> <amount>"));
                };
            }
            
            else
            {
                p.sendMessage(color("&cYou are not allowed to do this!"));
            };
            
            return true;
        };
    };
    
    @Override public void onDisable()
    {
        getServer().getScheduler().cancelTasks(plugin);
        print("I am now offline.");
    };
    
    String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };
    
    void print(final String d)
    {
        System.out.println("(Fire Crackers): " + d);
    };
};