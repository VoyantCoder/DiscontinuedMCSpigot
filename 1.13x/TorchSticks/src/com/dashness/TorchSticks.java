
// Author: Dashie
// Version: 1.0

package com.dashness;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class TorchSticks extends JavaPlugin
{
    final List<Material> torch_blocks = new ArrayList<>();
    final List<Material> drop_items = new ArrayList<>();
    
    int drop_chance, max_drops;
    String obtain_permission;    
    
    protected class Events implements Listener
    {
        final Random rand = new Random();
        
        @EventHandler public void onPlayerBlockBreak(final BlockBreakEvent e)
        {
            final Player p = (Player) e.getPlayer();
            
            if (p.hasPermission(obtain_permission))
            {
                if (torch_blocks.contains(e.getBlock().getType()))
                {
                    if (rand.nextInt(100) + 1 <= drop_chance)
                    {
                        final Location location = (Location) p.getLocation();
                        final World world = (World) p.getWorld();
                        
                        for (int i = 0; i < rand.nextInt(max_drops); i += 1)
                        {
                            world.dropItem(location, new ItemStack(drop_items.get(rand.nextInt(drop_items.size())), 1));
                        };
                        
                        e.setDropItems(false);
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
                print("You can only do this as a player!");
                return false;
            };
            
            final Player p = (Player) s;
            
            if (!p.hasPermission("admin"))
            {
                p.sendMessage(color("&cYou have insufficient permissions!"));
                return false;
            };
            
            if (as.length >= 1)
            {
                if (as[0].equalsIgnoreCase("reload"))
                {
                    p.sendMessage(color("&a>>> Working ...."));
                    
                    LoadConfiguration();
                    
                    p.sendMessage(color("&a>>> Done!"));
                    return true;
                };
            };
            
            p.sendMessage(color("&cInvalid syntax, correct syntax: &4&o/torchsticks reload"));
            return true;
        };
    };
    
    @Override public void onEnable()
    {
        print("I am loading up ....");
        
        LoadConfiguration();
        
        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("torchsticks").setExecutor(new Commands());
        
        print
        (
            (
                "\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\n" +
                " Author: Dashie \n" +
                " Version: 1.0 \n" +
                " ----------------------------------- \n" +
                " Contact: KvinneKraft@protonmail.com \n" +
                " Github: https://github.com/KvinneKraft \n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-"
            )
        );
        
        print("I am up and running!");
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
        
        torch_blocks.clear();
        drop_items.clear();
        
        try
        {
            obtain_permission = config.getString("torch-properties.obtain-permission");
            
            drop_chance = config.getInt("torch-properties.drop-chance");
            max_drops = config.getInt("torch-properties.max-drops");
            
            for (final String substance : config.getStringList("torch-properties.torch-blocks"))
            {
                try
                {
                    final Material substanterium = Material.getMaterial(substance);
                    torch_blocks.add(substanterium);
                }
                
                catch (final Exception e)
                {
                    print("(Torch Sticks): An invalid Torch Block was found in the configuration file.");
                };
            };
            
            for (final String substance : config.getStringList("torch-properties.drop-items"))
            {
                try
                {
                    final Material substanterium = Material.getMaterial(substance);
                    drop_items.add(substanterium);
                }
                
                catch (final Exception e)
                {
                    print("(Torch Sticks): An invalid Drop Item was found in the configuration file.");
                };
            };
        }
        
        catch (final Exception e)
        {
            print("A configuration error had occurred!");
        }
    };
    
    @Override public void onDisable()
    {
        print("I am now dead!");
    };
    
    protected String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };
    
    protected void print(final String d)
    {
        System.out.println("(Torch Sticks): " + d);
    };
};