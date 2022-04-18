
// Since this is a plugin for my own minecraft server. (kvinnekraft.serverminer.com) I will be 
// using static (hard coded) values, because it is only for personal use. ^

// Author: Dashie
// Version: 1.0

package com.philosophy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Cod;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.PufferFish;
import org.bukkit.entity.Salmon;
import org.bukkit.entity.TropicalFish;
import org.bukkit.plugin.java.JavaPlugin;

public class Consilience extends JavaPlugin
{
    public static FileConfiguration config = (FileConfiguration) null;
    public static JavaPlugin plugin = (JavaPlugin) null;
    public static Economy econ = (Economy) null;
    
    @Override public void onEnable()
    {
        Freya.print("Doing some necessities ....");
        
        plugin = (JavaPlugin) this;
        econ = (Economy) getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        
        Configuration.LoadConfiguration();
        
        getServer().getPluginManager().registerEvents(new EventsHandler(), plugin);        
        
        final String[] commands = new String[] 
        { 
            "discord", "github", "shop", "staff", "rules", "bee", "back", "whyvote"
        };
       
        final SimplisticHandler simplistic = new SimplisticHandler();        
        
        for (String command : commands)
        {
            getCommand(command).setExecutor(simplistic);
        };
        
        getCommand("spawn").setExecutor(new Spawn());        
        
        getServer().getScheduler().runTaskTimerAsynchronously
        (
            plugin, 
                
            new Runnable() 
            { 
                @Override public void run() 
                { 
                    for (final Entity entity : getServer().getWorld("world").getEntities())
                    {
                        if (entity instanceof Salmon || entity instanceof PufferFish || entity instanceof TropicalFish || entity instanceof Cod || entity instanceof Item)
                        {
                            getServer().getScheduler().runTask
                            (
                                plugin,
                                    
                                new Runnable()
                                {
                                    @Override public void run()
                                    {
                                        entity.remove();
                                    };
                                }
                            );
                        };
                    };
                }; 
            },
            
            0,
            280 * 20
        );
        
        getServer().getScheduler().runTaskTimerAsynchronously
        (
            plugin,
            
            new Runnable()
            {
                @Override public void run()
                {
                    getServer().getScheduler().runTask
                    (
                        plugin,

                        new Runnable()
                        {
                            @Override public void run()
                            {
                                getServer().broadcastMessage(Freya.color("&aAnyone with over &6&l15.000$ &ain-game money will be charged with &6&l8.5% &ataxes with in the next 1 minute!"));
                            };
                        }
                    );                    
                    
                    getServer().getScheduler().runTaskLaterAsynchronously
                    (
                        Consilience.plugin,
                            
                        new Runnable()
                        {
                            @Override public void run()
                            {
                                for (final Player p : getServer().getOnlinePlayers())
                                {
                                    if (econ.getBalance(p) > 15000)
                                    {
                                        getServer().getScheduler().runTask
                                        (
                                            plugin,

                                            new Runnable()
                                            {
                                                @Override public void run()
                                                {
                                                    final double amount = econ.getBalance(p) * 0.085;

                                                    p.sendMessage(Freya.color("&aYou have paid &6&l" + (int) amount + "&6$ &afor taxes, thank you!"));

                                                    econ.withdrawPlayer(p, amount);
                                                };
                                            }
                                        );
                                    };
                                };
                            };
                        },
                            
                        60 * 20
                    );                  
                };
            },
            
            60 * 30,
            (60 * 60) * 20
        );
        
        Freya.print("Done!");
    };
    
    @Override public void onDisable()
    {
        getServer().getScheduler().cancelTasks(plugin);
        Freya.print("Oh, I have been terminated.");
    };
};