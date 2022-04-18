
// Author: Dashie
// Version: 1.0

package com.philosophy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class EventsHandler implements Listener
{
    @EventHandler public void onJoin(final PlayerJoinEvent e)
    {
        Authenticate.onPlayerJoin(e);
    };
    
    @EventHandler public void onQuit(final PlayerQuitEvent e)
    {
        Authenticate.onPlayerQuit(e);
    };
    
    @EventHandler public void onPlayerDeath(final PlayerDeathEvent e)
    {
        SimplisticHandler.back_locations.put((Player) e.getEntity(), (Location) e.getEntity().getLocation());       
        
        e.getEntity().sendMessage(Freya.color("&7Ssshhhh, type &8/back &7to go back to your death location!"));
        
        DashHeads.onDeath(e);
    };
    
    @EventHandler public void onPlayerRespawn(final PlayerRespawnEvent e)
    {
        Spawn.onPlayerRespawn(e);
    };
    
    @EventHandler public void onEntityDamageByEntity(final EntityDamageByEntityEvent e)
    {
        Organisms.onEntityAttack(e);
    };
    
    @EventHandler public void onCreatureSpawn(final CreatureSpawnEvent e)
    {
        Organisms.onEntitySpawn(e);
    };
    
    private boolean scramblerActive = false, canScramble = false;
    private String scrambleWord, scrambleWordAnswer;
    
    private final List<String> word_list = Arrays.asList
    (
        new String[]
        {
            "BirdEgg", "Economy", "Conclusive", "Theoretical", "Interpretation",
            "TurdNuggets", "Dashie", "Philosophical", "Philosophy", "Nostalgia",
            "FluffyBalls", "ILoveYou", "Busses", "Maggots", "Worlds", "FlippingChair",
            "Decisive", "Listener", "MobsAreCool", "ThisIsAHardOneToSolve", "Yes",
            "Tremendous", "Globalization", "WorldOrder", "DashSociety", "KvinneKraft",
            "FlyingNuts", "Walnut", "HeIsAWurm", "YouCanNotGuessThis", "ILOVEYOU", 
            "Metaphorical", "Anthropologist", "Metaphysicist", "Metacorporal", "CIsBabe"
        }
    );    
    
    @EventHandler public void onPlayerChatEvent(final AsyncPlayerChatEvent e)
    {
        final Player p = (Player) e.getPlayer();
        
        if (!scramblerActive)
        {   
            Bukkit.getScheduler().runTaskTimerAsynchronously
            (
                Consilience.plugin, 

                new Runnable()
                {
                    private final Server server = (Server) Bukkit.getServer();

                    @Override public void run()
                    {
                        canScramble = true;
                        
                        server.getScheduler().runTaskLaterAsynchronously
                        (
                            Consilience.plugin,
                                
                            new Runnable()
                            {
                                @Override public void run()
                                {
                                    if (canScramble)
                                    {   
                                        server.broadcastMessage(Freya.color("&eNobody had solved the word, the word was &6" + scrambleWordAnswer + "&e!"));
                                        
                                        canScramble = false;
                                    };
                                };
                            },
                            
                            (10 * 60) * 20
                        );
                        
                        final List<Character> list = new ArrayList<>();
                        final String word = word_list.get(new Random().nextInt(word_list.size()));        

                        for (final char c : word.toCharArray())
                        {
                            list.add(c);
                        };

                        Collections.shuffle(list);

                        final StringBuilder builder = new StringBuilder();

                        for (final char c : list)
                        {
                            builder.append(c);
                        };                        
                        
                        scrambleWordAnswer = word;
                        scrambleWord = builder.toString();
                        
                        server.broadcastMessage(Freya.color("&eUnscramble this &6" + scrambleWord + " &etremendous word for &6200$ &e!!"));
                    };
                },

                0, 
                (46 * 60) * 20  
            );
            
            scramblerActive = true;            
        }
        
        else
        {
            if (canScramble && e.getMessage().contains(scrambleWordAnswer))
            {   
                Bukkit.getServer().broadcastMessage(Freya.color("&eThe user &6" + e.getPlayer().getName() + " &ehas solved the scrambled word! It was &6" + scrambleWordAnswer + "&e!"));
                
                p.sendMessage(Freya.color("&eYou have been given &6200&6&l$ &efor unraveling that tremendous word!"));
                
                Consilience.econ.depositPlayer(p, 200);
                e.setCancelled(true);          
                
                canScramble = false;
                
                return;
            };
        };
        
        if (((String)e.getMessage()).startsWith("#") && p.hasPermission("staff"))
        {
            Bukkit.getScheduler().runTaskAsynchronously
            (
                Consilience.plugin,
                
                new Runnable()
                {
                    @Override public void run()
                    {
                        final String m = e.getMessage().replaceFirst("#", Freya.color("&8([&bStaff Chat&8] &d%p%&8): &b"));
                        
                        for (final Player _p : Bukkit.getOnlinePlayers())
                        {
                            if (_p.hasPermission("staff"))
                            {
                                _p.sendMessage(m.replace("%p%", p.getName()));
                            };
                        };
                    };
                }
            );
            
            e.setCancelled(true);
            return;
        }
        
        else if (p.getName().toLowerCase().equals("majesty_freya"))
        {
            e.setMessage(Freya.color("&e" + e.getMessage()));
        }
        
        else if (p.getName().toLowerCase().equals("jorrit777"))
        {
            e.setMessage(Freya.color("&9" + e.getMessage()));
        }
        
        else if (p.getName().toLowerCase().equals("leonie42"))
        {
            e.setMessage(Freya.color("&9" + e.getMessage()));
        }        
        
        else if (p.getName().toLowerCase().equals("siul200311"))
        {
            e.setMessage(Freya.color("&c" + e.getMessage()));
        };
    };
};