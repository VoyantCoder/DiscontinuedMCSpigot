
// Author: Dashie
// Version: 1.0

package com.swords;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Withering extends JavaPlugin implements Listener, CommandExecutor
{
    private String admin_permission = null;
    
    private int duration, amplifier;
    
    private void loadConfigData()
    {
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        admin_permission = config.getString("command-permission");
        
        amplifier = config.getInt("potion-amplifier");        
        duration = config.getInt("potion-duration") * 20;
    };
    
    @Override public boolean onCommand(final CommandSender s, final Command c, String a, final String[] as)
    {
        if (!(s instanceof Player))
        {
            return false;
        };
        
        Player p = (Player) s;
        
        if(!(p.hasPermission(admin_permission)))
        {
            p.sendMessage(color("&cYou may not use this command!"));
            return false;
        }
        
        else if(as.length >= 1)
        {
            a = as[0].toLowerCase();
            
            if(a.equals("give"))
            {
                Player g = p;
                
                if(as.length >= 2)
                {
                    g = getServer().getPlayerExact(as[1]);
                    
                    if(g == null)
                    {
                        p.sendMessage(color("&cThe player specified must be online."));
                        return false;
                    };
                };
                
                g.getInventory().addItem(sword_item);

                final String sword_name = sword_item.getItemMeta().getDisplayName();
                
                if(g.equals(p))
                {
                    g.sendMessage(color("&aYou have given yourself a " + sword_name + "&a!"));
                }

                else
                {
                    p.sendMessage(color("&aYou have given &b" + g.getName() + " &aa " + sword_name + "&a!"));
                    g.sendMessage(color("&aYou have received a " + sword_name + "&a!"));
                };

                return true;                
            }
            
            else if(a.equals("reload"))
            {
                p.sendMessage(color("&e>>> &aReloading ...."));

                loadConfigData();

                p.sendMessage(color("&e>>> &aDone!"));

                return true;
            };            
        };
        
        p.sendMessage(color("&cThe correct syntax is as follows: &7/withersword [reload | give] <player>"));
        return false;
    };
    
    private FileConfiguration config = (FileConfiguration) null;
    private JavaPlugin plugin = (JavaPlugin) this;
    
    @Override public void onEnable()
    {
        print("Plugin is loading ....");
        
        saveDefaultConfig();
        loadConfigData();
        
        ItemMeta sword_meta = sword_item.getItemMeta();
        
        sword_meta.setDisplayName(color("&8Sword O\' Withering"));
        sword_meta.setUnbreakable(true);        
        
        sword_meta.setLore
        (
            Arrays.asList
            (
                new String[] 
                { 
                    color("&7&oWither your enemies away!") 
                }
            )
        );
        
        sword_item.setItemMeta(sword_meta);
        
        getServer().getPluginManager().registerEvents(this, plugin);
        getCommand("withersword").setExecutor(plugin);
        
        print("Plugin has been loaded!");
    };
    
    @Override public void onDisable()
    {
        print("Plugin has been disabled!");
    };
    
    private final ItemStack sword_item = new ItemStack(Material.STONE_SWORD, 1);
    
    @EventHandler public void onEntityDamage(EntityDamageByEntityEvent e)
    {
        if(!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof LivingEntity))
        {
            return;
        };
        
        final Player p = (Player) e.getDamager();

        if (p.getInventory() == null || p.getInventory().getItemInMainHand() == null || !p.getInventory().getItemInMainHand().equals(sword_item))
        {
            print("Errur");
            return;
        };
            
        final LivingEntity living_entity = (LivingEntity) e.getEntity();
        
        living_entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, duration, amplifier));        
        living_entity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration, amplifier));
        living_entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, amplifier));
        living_entity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, duration, amplifier));
        living_entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, amplifier));         
    };
    
    private String color(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
    
    private void print(String str)
    {
        System.out.println("(Wither Swords): " + str);
    };
};