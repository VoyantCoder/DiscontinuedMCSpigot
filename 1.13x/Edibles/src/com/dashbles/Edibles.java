
// Author: Dashie
// Version: 1.0

package com.dashbles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Edibles extends JavaPlugin implements CommandExecutor, Listener
{
    @Override public void onEnable()
    {
        print("Loading the plugin ....");
        
        LoadConfiguration();
        
        getServer().getPluginManager().registerEvents(this, plugin);
        getCommand("edibles").setExecutor(plugin);
        
        print("Plugin has been enabled!");
    };
    
    private FileConfiguration config = (FileConfiguration) null;
    private final JavaPlugin plugin = (JavaPlugin) this;
    
    private void LoadConfiguration()
    {
        saveDefaultConfig();
        
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        reload_perm = config.getString("properties.permissions.reload");
        give_perm = config.getString("properties.permissions.give");
        
        if(edible == null)
        {
            edible = new ItemStack(Material.GREEN_DYE, 1);
            
            ItemMeta meta = edible.getItemMeta();
            
            meta.setDisplayName(color("&d&lD&dashies &d&lE&ddible"));
            meta.setLore
            (
                Arrays.asList
                (
                    new String[] 
                    { 
                        color("&a&oA herb so powerful &d<3")
                    }
                )
            );
            
            edible.setItemMeta(meta);
        };
        
        op_bypass = config.getBoolean("properties.op-bypass");
        cooldown = config.getInt("properties.cooldown") * 20;
        
        if(effects.size() < 1)
        {
            effects.addAll
            (
                Arrays.asList
                (
                    new PotionEffect[] 
                    {
                        new PotionEffect(PotionEffectType.NIGHT_VISION, 35 * 20, 1),
                        new PotionEffect(PotionEffectType.CONFUSION, 12 * 20, 1),
                        new PotionEffect(PotionEffectType.HUNGER, 60 * 20, 1),
                        new PotionEffect(PotionEffectType.SPEED, 30 * 20, 2),
                    }
                )
            );
        };
    };
    
    private final List<PotionEffect> effects = new ArrayList<>();    
    private final List<Player> players = new ArrayList<>();
    
    @EventHandler public void onConsumption(PlayerInteractEvent e)
    {
        if(e.getItem() == null || !e.getItem().hasItemMeta() || !e.getItem().getItemMeta().hasLore() || !e.getItem().getItemMeta().getLore().equals(edible.getItemMeta().getLore()))
        {
            return;
        };
        
        final Player p = (Player) e.getPlayer();
        
        if(players.contains(p))
        {
            p.sendMessage(color("&cYou must calm down a bit, you may overdose!"));
            return;
        };
        
        p.playSound(p.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 30, 30);
        p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 30, 30);
        
        p.addPotionEffects(effects);                
        
        getServer().getScheduler().runTaskLater
        (
            plugin, 
            
            new Runnable() 
            { 
                @Override public void run() 
                { 
                    final int a = e.getItem().getAmount();

                    if(a > 1) 
                    {        
                        e.getItem().setAmount(a - 1);
                    }

                    else
                    {
                        p.getInventory().removeItem(e.getItem());            
                    };                    
                };
            }, 
        
            10
        );
        
        p.sendMessage(color("&aYou have consumed a Dashble, get ready for an intensifying trip!"));
        
        if(op_bypass && p.isOp())
        {
            return;
        };
        
        players.add(p);
        
        getServer().getScheduler().runTaskLater
        (
            plugin, 
            
            new Runnable() 
            { 
                @Override public void run() 
                {
                    if(players.contains(p))
                    {
                        players.remove(p);
                        
                        if(p.isOnline())
                        {
                            p.sendMessage(color("&aYou may now eat another edible!"));
                            return ;
                        };
                    };
                }; 
            },
            
            cooldown
        );
    };
    
    private String give_perm, reload_perm;
    private ItemStack edible = null;
   
    private boolean op_bypass;
    private int cooldown;    
    
    @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
    {
        if(!(s instanceof Player))
        {
            print("Must be an in-game executor!");
            return false;
        };
        
        final Player p = (Player) s;
        
        if(!p.hasPermission(give_perm) && !p.hasPermission(reload_perm))
        {
            p.sendMessage(color("&cYe are not supposed to use this, huh?"));
            return false;
        };
        
        if(as.length >= 1)
        {
            final String sid = as[0].toLowerCase();            
            
            if(sid.equals("give") && p.hasPermission(give_perm))
            {
                Player t = (Player) p;

                if(as.length >= 2)
                {
                    t = getServer().getPlayerExact(as[1]);

                    if(t == null)
                    {
                        p.sendMessage(color("&cThe specified player must be online!"));
                        return false;
                    };
                };

                if(t.equals(p))
                {
                    p.sendMessage(color("&aYou have given yourself an edible!"));
                }

                else
                {
                    p.sendMessage(color("&aYou have given &b" + t.getName() + " &aan edible!"));
                    t.sendMessage(color("&aYou have been given an edible by &b" + p.getName() + " &a!"));
                };

                t.getInventory().addItem(edible);
            }

            else if(sid.equals("reload") && p.hasPermission(reload_perm))
            {
                p.sendMessage(color("&e>>> &aReloading the configuration ...."));

                LoadConfiguration();

                p.sendMessage(color("&e>>> &aThe configuration has been reloaded!"));
            }
        }
        
        else
        {
            p.sendMessage(color("&cCorrect usage: &7/dashbles [give | reload] <player>"));
            return false;            
        };
            
        return true;
    };
    
    @Override public void onDisable()
    {
        print("The plugin has been disabled!");
    };
    
    private String color(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
    
    private void print(String str)
    {
        System.out.println("(Dashies Edibles): " + str);
    };
};