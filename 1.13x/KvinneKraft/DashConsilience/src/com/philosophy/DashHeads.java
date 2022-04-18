
// Author: Dashie
// Version: 1.0

package com.philosophy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class DashHeads
{
    private final static List<String> lores = new ArrayList<>();
    
    public static void onDeath(PlayerDeathEvent e)
    {
        final Player p = (Player) e.getEntity();        
        final double min_price = 2500;        
        
        double take_price = min_price;
        
        if (Consilience.econ.getBalance(p) < min_price)
        {
            if (Consilience.econ.getBalance(p) <= 0)
            {
                p.sendMessage(Freya.color("&cYou have lost no money because you have none!"));
            }
            
            else
            { 
                take_price = (double) Consilience.econ.getBalance(p);
            };
        };                
        
        if (e.getEntity().getKiller() instanceof Player)
        {
            final Player k = (Player)e.getEntity().getKiller();
            
            if (k.equals(p))
            {
                return;
            };
            
            final Random r = new Random();
            
            k.sendMessage(Freya.color("&eYou have been given &6&l150&6$ &efor killing &6" + p.getName() + "&e!"));
            
            if (r.nextInt() < 40)
            {
                final ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
                final SkullMeta meta = (SkullMeta) item.getItemMeta();

                item.getItemMeta().setDisplayName(Freya.color("&e&l" + p.getName() + "\'s Head"));
                
                if (lores.size() < 1)
                {
                    lores.addAll
                    (
                        Arrays.asList
                        (
                            new String[]
                            {
                                "&e&oYou have been given this as a trophy!", 
                                "&7&oYou sure as heck know what is creepy >.<",
                                "&8&oOh my goddess, did you just really keep this head?",
                                "&e&lFIRST ROUND DECAPITATION, YEHHH!!!",
                                "&dJust drink the bloody insides ;)",
                                "&dHe loved you ;c",
                                "&dShe loved you ;c",
                                "&dWhy this person?",
                                "&dMwhahahaha, you did great, you earned it!",
                            }
                        )
                    );
                };
                
                meta.setLore
                (
                    Arrays.asList
                    (
                        new String[] 
                        {
                            Freya.color(lores.get(r.nextInt(lores.size())))
                        }
                    )
                );

                meta.setOwningPlayer(p);  
                item.setItemMeta(meta);
                
                k.getInventory().addItem(item);
            };
            
            Consilience.econ.depositPlayer(k, 150);             
        };
        
        if (Consilience.econ.getBalance(p) > 0)
        {
            final double stake_price = take_price;

            Bukkit.getScheduler().runTaskLaterAsynchronously
            (
                Consilience.plugin, 

                new Runnable() 
                { 
                    @Override public void run() 
                    {
                        if (p.isOnline())
                        {
                            p.sendMessage(Freya.color("&cYou have lost &4&l" + stake_price + "&4$ &cbecause you had died!"));
                        };
                    }; 
                },

                1 * 20
            );

            Consilience.econ.withdrawPlayer(p, take_price);        
        };
    };
};