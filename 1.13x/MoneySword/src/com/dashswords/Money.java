
// Author: Dashie
// Version: 1.0

package com.dashswords;

import java.text.DecimalFormat;
import java.util.Arrays;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Money extends JavaPlugin implements Listener, CommandExecutor
{
    private FileConfiguration config = (FileConfiguration) null;    
    private final JavaPlugin plugin = (JavaPlugin) this;
    private Economy econ = (Economy) null;
    
    private boolean InitializeVault() 
    {
        if (getServer().getPluginManager().getPlugin("Vault") == null) 
        {
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        
        if (rsp == null) 
        {
            return false;
        }
        
        econ = rsp.getProvider();
        
        return econ != null;
    }    
    
    @Override public void onEnable()
    {
        print("Plugin is loading ....");
        
        if (!InitializeVault())
        {
            print("Vault is missing, get it here: https://www.spigotmc.org/resources/vault.34315/");
            getServer().getPluginManager().disablePlugin(this);
            return;            
        };
        
        LoadConfiguration();
        
        getServer().getPluginManager().registerEvents(this, plugin);
        getCommand("moneysword").setExecutor(this);
        
        print("Plugin has been enabled!");
    };
    
    private final ItemStack sword_item = new ItemStack(Material.DIAMOND_SWORD, 1);
    
    private String admin_permission, use_permission, reward_message;
    private double price_min, price_max;
    
    private boolean isDoubleValue(String supposed)
    {
        try
        {
            Double.parseDouble(supposed);
            return true;
        } 
        
        catch (Exception e)
        {
            return false;
        }
    };
    
    private void LoadConfiguration()
    {
        saveDefaultConfig();
        
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        if (!sword_item.hasItemMeta())
        {
            final ItemMeta meta = (ItemMeta) sword_item.getItemMeta();
            
            meta.setDisplayName
            (
                color
                (
                    config.getString("properties.money-sword-name")
                )
            );
            
            meta.setLore
            (
                Arrays.asList
                (
                    new String[] 
                    { 
                        color(config.getString("properties.money-sword-lore")) 
                    }
                )
            );
            
            sword_item.setItemMeta(meta);
        };
        
        final String[] values = config.getString("properties.money-sword-money-range").replace(" ", "").split("-");
        
        if(!isDoubleValue(values[0]) || !isDoubleValue(values[1]) || values.length < 1)
        {
            print("Invalid price format received, using defaults.");
         
            price_min = 5;
            price_max = 500;
        }
        
        else if(values.length == 1)
        {
            price_min = 0;
        };
        
        price_min = Double.valueOf(values[0]);
        price_max = Double.valueOf(values[1]);
        
        use_permission = config.getString("properties.money-sword-use-permission");
        admin_permission = config.getString("properties.money-sword-admin-permission");
    
        reward_message = color(config.getString("properties.money-sword-reward-message"));
    };
    
    @EventHandler public void onEntityDeath(final EntityDeathEvent e)
    {
        if (!(e.getEntity().getKiller() instanceof Player))
        {
            return;
        };
        
        final Player p = (Player) e.getEntity().getKiller();
        
        if(!p.hasPermission(use_permission) || p.getInventory().getItemInMainHand() == null || !p.getInventory().getItemInMainHand().equals(sword_item))
        {
            return;
        };
        
        final DecimalFormat reformat = new DecimalFormat("#.##");
        
        final double reward = Double.parseDouble
        (
            reformat.format
            (
                (double)(Math.random() * (price_max - price_min) + 1)
            )
        );
        
        econ.depositPlayer(p, reward);
  
        p.sendMessage(reward_message.replace("%money%", String.valueOf(reward)));
    };
    
    @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
    {
        if (!(s instanceof Player))
        {
            print("You may only run this command as a player.");
            return false;
        };
        
        final Player p = (Player) s;
        
        if (!p.hasPermission(admin_permission))
        {
            p.sendMessage(color("&cYou may not use this command."));
            return false;
        }
        
        else if (as.length >= 1 && as[0].equalsIgnoreCase("reload"))
        {
            p.sendMessage(color("&e>>> &aReloading ...."));
            
            LoadConfiguration();
            
            p.sendMessage(color("&e>>> &aDone!"));
        }
        
        else if (as.length >= 1 && as[0].equalsIgnoreCase("give"))
        {
            Player t = p;
            
            if(as.length >= 2)
            {
                t = (Player) getServer().getPlayerExact(as[1]);

                if(t == null)
                {
                    p.sendMessage(color("&cThe player specified must be online!"));
                    return false;
                };
            };
            
            t.getInventory().addItem(sword_item);
            
            if (!p.equals(t))
            {
                p.sendMessage(color("&aYou have given &b" + t.getName() + " &aa worthy sword!"));
                t.sendMessage(color("&aYou have been given a worthy sword!"));
            }
            
            else
            {
                p.sendMessage(color("&aYou have given yourself a worthy sword!"));
            };
        }
        
        else
        {
            p.sendMessage(color("&cUsage: &7/moneysword [give | reload] <player>"));
            return false;
        };
        
        return true;
    };
    
    @Override public void onDisable()
    {
        print("Plugin has been disabled!");
    };
    
    private String color(String line)
    {
        return ChatColor.translateAlternateColorCodes('&', line);
    };
    
    private void print(String line)
    {
        System.out.println("(Money Swords): " + line);
    };
};