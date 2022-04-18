
// Author: Dashie
// Version: 1.0

package dash.recoded;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BetterTrolls extends JavaPlugin
{
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
        
        Caching.troll_permission = config.getString("troll-permission");
        Caching.admin_permission = config.getString("admin-permission");
    };
    
    @Override public void onEnable()
    {
        print("The Dash A.I. is starting ....");
        
        LoadConfiguration();
        
        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("bettertrolls").setExecutor(new Commands());
        
        print("The Dash A.I. is now online!");
    };
    
    protected static class Caching
    {
        static final List<Player> freeze_queue = new ArrayList<>();
        static final List<Player> blind_queue = new ArrayList<>();
        
        static String troll_permission, admin_permission;
    };    
    
    protected class Events implements Listener
    {
        @EventHandler public void onPlayerMovement(final PlayerMoveEvent e)
        {
            final Player p = (Player) e.getPlayer();
            
            if (Caching.freeze_queue.contains(p))
            {
                e.setCancelled(true);
            };
        };
    };
    
    protected class Commands implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("Only players may execute this command!");
                return false;
            };
            
            final Player p = (Player) s;
            
            if (as.length >= 1)
            {
                a = as[0].toLowerCase();
                
                if (p.hasPermission(Caching.admin_permission))
                {
                    if (a.equals("reload"))
                    {
                        p.sendMessage(color("&a>>> Working on it ...."));
                        
                        LoadConfiguration();
                        
                        p.sendMessage(color("&a>>> Done!"));
                        
                        return true;
                    };
                };

                if (p.hasPermission(Caching.troll_permission))
                {
                    if (as.length >= 2)
                    {
                        final Player victim = (Player) getServer().getPlayerExact(as[1]);
                        
                        if (victim == null || !victim.isOnline())
                        {
                            p.sendMessage(color("&cThe player must be online!"));
                            return false;
                        }
                        
                        else if (a.equals("freeze"))
                        {
                            if (Caching.freeze_queue.contains(victim))
                            {
                                p.sendMessage(color("&aThe player &e" + victim.getName() + " &ais now no longer frozen!"));
                                Caching.freeze_queue.remove(victim);
                            }
                            
                            else
                            {
                                p.sendMessage(color("&aThe player &e" + victim.getName() + " &ahas been frozen!"));
                                Caching.freeze_queue.add(victim);
                            };
                            
                            return true;
                        }

                        else if (a.equals("smite"))
                        {
                            getServer().getScheduler().runTask
                            (
                               plugin,
                               
                               new Runnable()
                               {
                                   @Override public void run()
                                   {
                                        for (int v = 0; v < 8; v += 1)
                                        {
                                            victim.getWorld().strikeLightning(victim.getLocation());
                                        };                                       
                                   };
                               }
                            );
                            
                            p.sendMessage(color("&aThe player &e" + victim.getName() + " &ahas been struck!"));
                            return true;
                        }

                        else if (a.equals("kill"))
                        {
                            victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 20, 1000));
                            victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 20, 1000));
                            victim.setHealth(0);
                            
                            p.sendMessage(color("&aThe player &e" + victim.getName() + " &ahas been assassinated!"));
                            
                            return true;
                        }

                        else if (a.equals("blind"))
                        {
                            if (Caching.blind_queue.contains(victim))
                            {
                                victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 393939, 1000));                                
                                p.sendMessage(color("&aThe player &e" + victim.getName() + " &ais no longer blind!"));
                                Caching.blind_queue.remove(victim);
                            }
                            
                            else
                            {
                                if (victim.hasPotionEffect(PotionEffectType.BLINDNESS))
                                {
                                    victim.removePotionEffect(PotionEffectType.BLINDNESS);
                                };
                                
                                p.sendMessage(color("&aThe player &e" + victim.getName() + " &ais now blind!"));
                                Caching.blind_queue.add(victim);
                            };
                            
                            return true;
                        }
                        
                        else if (a.equals("hole"))
                        {
                            return true; // For in the future.
                        }

                        else if (a.equals("spam"))
                        {
                            getServer().getScheduler().runTaskAsynchronously
                            (
                                plugin, 
                                    
                                new Runnable() 
                                { 
                                    @Override public void run() 
                                    { 
                                        final String message = color("&b&l&kajklhkjlsfkljasfgjklasjklgajklsgjlkagsdljkajlkgjlkadghjkldajkladjlkhjkladjkldhkjldajhklkljha;djkl;ahdjkldglkjklj;jkl;ajkl;");
                                        
                                        for (int k = 0; k < 7400; k += 1)
                                        {
                                            victim.sendMessage(message);
                                        };
                                    }; 
                                }
                            );
                            
                            p.sendMessage(color("&aThe player &e" + victim.getName() + " &ais being bombarded with messages!"));
                            return true;
                        }

                        else if (a.equals("fake-op"))
                        {
                            getServer().broadcastMessage(color("&7&o[CONSOLE: Made " + victim.getName() + " a server operator]")); 
                            return true;
                        }

                        else if (a.equals("mob-wave"))
                        {
                            return true; // Later thing
                        }
                        
                        else if (a.equals("spook"))
                        {
                            p.sendMessage(color("&aYou have &f&lSPOOKED &e" + victim.getName() + " &a!"));
                            
                            victim.playSound(victim.getLocation(), Sound.ENTITY_WITHER_DEATH, 28, 28);
                            victim.playSound(victim.getLocation(), Sound.ENTITY_WITHER_DEATH, 28, 28);
                            victim.playSound(victim.getLocation(), Sound.ENTITY_WITHER_DEATH, 28, 28);
                            
                            victim.sendTitle(color("&f&lBOOOO!!!"), "");
                        };
                    };
                };
            };
            
            if (!p.hasPermission(Caching.admin_permission))
            {
                p.sendMessage(color("&cBad syntax, correct syntax: &4/troll [freeze | spook | smite | kill | blind | spam | fake-op] <player>"));            
            }
            
            else
            {
                p.sendMessage(color("&cBad syntax, correct syntax: &4/troll [reload | spook | freeze | smite | kill | blind | spam | fake-op] <player>"));
            };
            
            return true;
        };
    };
        
    @Override public void onDisable()
    {
        print("The Dash A.I. is now offline!");
    };
    
    String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };
    
    void print(final String d)
    {
        System.out.println("(Better Trolls): " + d);
    };
};